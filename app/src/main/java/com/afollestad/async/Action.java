package com.afollestad.async;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import java.util.Locale;

import apk.tool.patcher.util.Preferences;

/**
 * Класс для работы в асинхронном режиме.
 * Поддерживает очередь, а также 2 режима выполнения:
 * {@link Pool.Mode#MODE_SERIES} выполнение в режиме "один после другого"
 * {@link Pool.Mode#MODE_PARALLEL} выполнение в режиме "все сразу"
 *
 * @author Aidan Follestad (afollestad) и Artem Zhiganov (SnowVolf)
 */
public abstract class Action<RT> extends Base {
    private static final String TAG = "Action";

    private final Object LOCK = new Object();
    /**
     * Прогресс выполняемой задачи
     */
    public int progress;
    /**
     * Используется для постепенной отправки сообщений в UI.
     *
     * Нужен для быстротечных задач, которые подразумевают множественные вхождения
     * Например, если при выполнении задачи за 10 секунд нашлось 5000 совпадений, то нет смысла
     * оповещать пользователя о каждом из них, достаточно оповестить его о нахождении каждого 1000-го
     * совпадения, и о конечном числе совпадений. Эффект будет тот же, а головной боли - меньше.
     */
    protected int ETA;
    /**
     * Главный хандлер
     */
    private Handler mHandler;
    /**
     * Инстанс очереди
     */
    private Pool mPool;
    /**
     * Индекс текущей выполняемой задачи
     */
    private int mPoolIndex;
    /**
     * Флаг, оповещающий о том, что задача еще выполняется
     */
    private boolean mExecuting;
    /**
     * Флаг, оповещающий о том, что задача отменена
     */
    private boolean mCancelled;
    /**
     * Поток, в котором будет выполняться задача
     */
    private Thread mThread;
    /**
     * Флаг, оповещающий о том, что задача выполнена
     */
    private boolean mDone;
    /**
     * Результат задачи
     */
    private RT mResult;
    /**
     * Аргументы, передаваемые задаче
     */
    private String[] arguments;
    /**
     * Слушатель выполнения задачи. Оповещает UI поток об изменении состояния прогресса
     * @see OnExecutionListener#onProgressUpdate(Action, int)
     * @see OnExecutionListener#onEvent(String)
     */
    private OnExecutionListener progressUpdateListener;
    /**
     * Флаг, оповещающий о том, что задача не поддерживает параллельное выполнение
     */
    private boolean mSupportParallel;

    /**
     * Индекс текущей задачи внутри общей очереди
     * @return номер задачи
     */
    @IntRange(from = 0, to = Integer.MAX_VALUE)
    public final int getPoolIndex() {
        return mPoolIndex;
    }

    /**
     * Каждый таск должен иметь уникальный ID
     * @return ID текущего таска
     */
    @NonNull
    public abstract String id();

    /**
     * Запуск таска с нужными параметрами
     * @param params массив параметров
     * @return результат выполнения таска
     * @throws InterruptedException если выполнение заблокировано (например из-за нехватки памяти)
     */
    @WorkerThread
    @Nullable
    protected abstract RT run(String... params) throws InterruptedException;

    /**
     * Вызывается по окончанию выполнения задачи
     * @param result результат задачи
     */
    @UiThread
    protected void subscribe(@SuppressWarnings("UnusedParameters") @Nullable RT result) {
        // Optional
    }

    /**
     * Запуск выполнения задачи
     * - Проверяется, нет ли такой задачи в активной очереди
     * - Присваиваются флаги {@link Action#mExecuting}, {@link Action#mDone},
     *                       {@link Action#mCancelled}
     * - UI оповещается о том, что задача запущена
     * - Запускается новый поток, в котором выполняется задача {@link Action#run(String...)}
     * - Результат отдаётся через {@link Action#subscribe(Object)}
     * - UI оповещается о том, что задача выполнена
     * @throws IllegalStateException если задача с таким ID уже присутствует в очереди выполнения
     */
    @UiThread
    public final void execute() throws IllegalStateException {
        synchronized (LOCK) {
            if (mExecuting)
                throw new IllegalStateException("This action has already been executed.");
            mExecuting = true;
            mDone = false;
            mCancelled = false;

            if (mPool == null && mHandler == null) {
                LOG(Action.class, "Pool is null, creating action-level handler.");
                mHandler = new Handler();
            }
            LOG(Action.class, "Executing action at index %d (%s)...", getPoolIndex(), id());
            progressUpdateListener.onEvent(String.format(Locale.ENGLISH, "Executing action %d (%s)...", getPoolIndex(), id()));

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (Preferences.isLowMemoryMode()){
                            System.gc();
                        }
                        mResult = Action.this.run(getArguments());
                    } catch (InterruptedException e) {
                        LOG(Action.class, "Action at index %d (%s) was cancelled.", getPoolIndex(), id());
                        progressUpdateListener.onEvent(String.format(Locale.ENGLISH, "Action %d (%s) was cancelled.", getPoolIndex(), id()));
                        mCancelled = true;
                    }

                    mExecuting = false;
                    if (isCancelled()) {
                        LOG(Action.class, "Action at index %d (%s) was cancelled.", getPoolIndex(), id());
                        progressUpdateListener.onEvent(String.format(Locale.ENGLISH, "Action %d (%s) was cancelled.", getPoolIndex(), id()));
                        return;
                    }
                    post(new Runnable() {
                        @Override
                        public void run() {
                            LOG(Action.class, "Action at index %d (%s) finished executing!", getPoolIndex(), id());
                            progressUpdateListener.onEvent(String.format(Locale.ENGLISH, "Action %d (%s) finished executing!", getPoolIndex(), id()));
                            mDone = true;
                            subscribe(mResult);
                            mThread = null;
                            if (mPool != null)
                                mPool.pop(Action.this);
                        }
                    });
                }
            });
            mThread.start();
        }
    }

    /**
     * Блокирование главного потока, до того момента, пока задача не выполнится
     * @throws IllegalStateException если задача отсутствует в очереди выполнения
     */
    public final void waitForExecution() throws IllegalStateException {
        if (!isExecuting())
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Action at index %d (%s) is not currently executing.", getPoolIndex(), id()));
        while (isExecuting() && !isCancelled()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Отправка сообщений главному хандлеру
     * @param runnable содержимое сообщения
     */
    @WorkerThread
    public final void post(Runnable runnable) {
        synchronized (LOCK) {
            if (mPool == null) {
                mHandler.post(runnable);
            } else {
                mPool.post(runnable);
            }
        }
    }

    /**
     * Отмена задачи
     */
    public final void cancel() {
        synchronized (LOCK) {
            mCancelled = true;
            if (mThread != null)
                mThread.interrupt();
            mThread = null;
        }
    }

    /**
     * Проверка на выполнение задачи
     * @return true, если задача еще выполняется
     */
    public final boolean isExecuting() {
        synchronized (LOCK) {
            return mExecuting;
        }
    }

    /**
     * Проверка на отмену задачи
     * @return true, если задача отменена
     */
    public final boolean isCancelled() {
        synchronized (LOCK) {
            return mCancelled;
        }
    }

    /**
     * Проверка на завершение задачи
     * @return true, если задача завершена
     */
    public final boolean isDone() {
        return mDone;
    }

    /**
     * Получение результата выполнения задачи
     * @return результат
     */
    public RT getResult() {
        return mResult;
    }


    /**
     * Присваивание очереди выполнения
     * @param pool очередь
     * @param poolIndex индекс очереди
     * @throws IllegalStateException если задача из этой очереди уже используется в другом месте
     */
    protected final void setPool(@Nullable Pool pool,
                                 @IntRange(from = -1, to = Integer.MAX_VALUE) int poolIndex) throws IllegalStateException {
        synchronized (LOCK) {
            mPoolIndex = poolIndex;
            mCancelled = false;
            mExecuting = false;
            if (pool == null) {
                mPool = null;
                return;
            } else if (mPool != null) {
                throw new IllegalStateException(String.format("Action with ID %s is already in use by another Pool.", id()));
            }
            mPool = pool;
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %s", id(), mResult);
    }

    /**
     * Получение аргументов выполнения
     * @return массив аргументов
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * Присваивание аргументов выполнения
     * @param args аргументы
     */
    public void setArguments(String... args) {
        this.arguments = args;
    }

    /**
     * Присваивание слушателя выполнения задачи
     * @param listener слушатель выполнения
     */
    public void setListener(OnExecutionListener listener) {
        progressUpdateListener = listener;
    }

    /**
     * Оповещение слушателя {@link OnExecutionListener} об изменении прогресса
     * @param action выполняемая задача
     * @param progress прогресс выполнения задачи
     *
     * @see OnExecutionListener#onProgressUpdate(Action, int)
     */
    public void postProgress(Action action, int progress) {
        progressUpdateListener.onProgressUpdate(action, progress);
    }

    /**
     * Оповещение слушателя {@link OnExecutionListener} о каком-либо событии,
     * не привязанном к конкретной задаче
     * @param message сообщение
     *
     * @see OnExecutionListener#onEvent(String)
     */
    public void postEvent(String message) {
        progressUpdateListener.onEvent(message);
    }

    /**
     * Оповещение слушателя {@link OnExecutionListener} об ошибке,
     * произошедшей в процессе выполнения
     * @param err ошибка
     *
     * @see OnExecutionListener#onEvent(String)
     */
    public void postError(Exception err){
        progressUpdateListener.onExecutionError(getError(err));
    }

    /**
     * Получение содержимого ошибки в удобном для анализа виде
     * @param e ошибка
     * @return отчёт об ошибке, который можно просмотреть через
     *          {@link android.text.Html#fromHtml(String)} или
     *          {@link android.text.Html#fromHtml(String, int)}
     */
    protected String getError(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage())
                .append("<br><br><b>Caused by:</b><br><br>")
                .append(e.getCause())
                .append("<br><br><b>Stack Trace:</b><br><br>");
        for (StackTraceElement traceElement : e.getStackTrace()) {
            sb.append(traceElement).append("<br>");
        }
        Log.d(TAG, "getError() returned: " + sb);
        return sb.toString();
    }
}