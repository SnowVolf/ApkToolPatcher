package apk.tool.patcher.ui.modules.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.async.Action;
import com.afollestad.async.Async;
import com.afollestad.async.OnExecutionListener;
import com.afollestad.async.Result;
import com.afollestad.async.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import apk.tool.patcher.App;

import apk.tool.patcher.R;
import apk.tool.patcher.api.Project;
import apk.tool.patcher.entity.OnAsyncJobListener;
import apk.tool.patcher.entity.async.AsyncRepository;
import apk.tool.patcher.ui.modules.base.adapters.DialogControlsAdapter;
import apk.tool.patcher.ui.modules.base.adapters.LogAdapter;
import apk.tool.patcher.util.Cs;
import ru.svolf.melissa.model.ControlsItem;
import ru.svolf.melissa.model.LogItem;
//import android.support.v4.*;
//import androidx.multidex.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAsyncJobListener} interface
 * to handle interaction events.
 * Use the {@link AsyncFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AsyncFragment extends Fragment implements OnExecutionListener {
    private static final String TAG = "AsyncFragment";
    /**
     * Куда будет записываться лог
     */
    ArrayList<LogItem> mLogItems = new ArrayList<>();
    /**
     * Рутовая вьюха
     */
    private View rootView;
    /**
     * Сюда будет выводиться лог
     */
    private RecyclerView mLogView;
    /**
     * Базовый параметр
     */
    private String mBaseParam;
    /**
     * Флаг наличия базового параметра
     */
    private boolean mHasParam = false;
    /**
     * Массив из id наших тасков
     */
    private String[] mActionIds;
    /**
     * Нужен для оповещения хостовой активити
     *
     * @see OnAsyncJobListener
     */
    private OnAsyncJobListener mListener;
    /**
     * Папка проекта
     */
    private Project mProjectDir;
    /**
     * Таймер выполнения тасков
     */
    private Chronometer mChronometer;
    /**
     * Контроллер
     */
    private RecyclerView mControlsView;
    /**
     * Адаптер
     */
    private LogAdapter mAdapter;

    public AsyncFragment() {
        // Required empty public constructor
    }


    /**
     * Создаёт экземпляр фрагмента с нужными параметрами
     *
     * @param dir  текущий проект
     * @param args аргументы (id операций, которые нужно выполнить)
     *             если <code>hasParam == false</code>, то таски нужно брать начиная с 0, иначе с 1
     * @return экземпляр фрагмента AsyncFragment
     * @see Action
     */
    public static AsyncFragment newInstance(@NonNull Project dir, @Nullable CharSequence baseParam, @NonNull String... args) {
        Log.d(TAG, "newInstance() called with: dir = [" + dir + "], baseParam = [" + baseParam + "], args = [" + args + "]");
        AsyncFragment fragment = new AsyncFragment();
        Bundle options = new Bundle();
        options.putParcelable(Cs.ARG_PATH_NAME, dir);
        options.putBoolean(Cs.ARG_HAS_BASE_PARAM, baseParam != null);
        if (baseParam != null) {
            options.putString(Cs.ARG_BASE_PARAM, baseParam.toString());
        }

        options.putStringArray(Cs.ARG_ACTIONS, args);

        fragment.setArguments(options);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлейтим рутовую вьюху
        rootView = inflater.inflate(R.layout.fragment_async, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Находим наши виджеты по ID
        mChronometer = view.findViewById(R.id.info);
        mLogView = view.findViewById(R.id.list);
        mControlsView = view.findViewById(R.id.list_controls);
        mChronometer.setBase(SystemClock.elapsedRealtime());

        mLogView.setLayoutManager(new LinearLayoutManager(getContext()));
        mControlsView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new LogAdapter(mLogItems);
        mLogView.setAdapter(mAdapter);

        ArrayList<ControlsItem> controlsItems = new ArrayList<>();
        controlsItems.add(new ControlsItem(R.drawable.ic_cancel, App.bindString(R.string.cancel_all), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Завершаем все таски
                Async.cancelAll();
                Handler wait = new Handler();
                wait.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Эмитируем нажатие кнопки "НАЗАД", чтоб вернуться на предыдущий экран
                        getActivity().onBackPressed();
                    }
                }, 500);
            }
        }));
        DialogControlsAdapter controlsAdapter = new DialogControlsAdapter(controlsItems);
        controlsAdapter.setItemClickListener(new DialogControlsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ControlsItem menuItem, int position) {
                menuItem.getAction().onClick(null);
            }
        });
        mControlsView.setAdapter(controlsAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            // Присваиваем наши аргументы
            if (getArguments() != null) {
                mProjectDir = getArguments().getParcelable(Cs.ARG_PATH_NAME);
                Log.d(TAG, "onActivityCreated: mProjectDir = " + mProjectDir.getPath());

                mActionIds = getArguments().getStringArray(Cs.ARG_ACTIONS);
                Log.d(TAG, "onActivityCreated: mActionIds = " + Arrays.toString(mActionIds));

                mHasParam = getArguments().getBoolean(Cs.ARG_HAS_BASE_PARAM);
                Log.d(TAG, "onActivityCreated: mHasParam = " + mHasParam);
                if (mHasParam) {
                    mBaseParam = getArguments().getString(Cs.ARG_BASE_PARAM);
                }
            }
            replaceOccurrences();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAsyncJobListener) {
            mListener = (OnAsyncJobListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAsyncJobListener");
        }
    }

    @Override
    public void onDestroyView() {
        // Завершаем все таски, если что-то выполняется
        Async.cancelAll();
        // ОБЯЗАТЕЛЬНО, чистим за собой!
        // иначе через несколько показов этого фрагмента, память аппы потечёт словно
        // малолетняя сучка при виде Егора Крида
        rootView = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Подчищаем за собой
        mListener = null;
    }

    // Обновляем лог
    private void updateUI(Action action, int progress) {
        mLogItems.add(new LogItem("i", String.format(Locale.ENGLISH, "%s: %d matches replaced", action.id(), progress)));
        // Т.к. таск выполняется не в UI потоке, нам нужно прокинуть событие в UI
        mLogView.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 5) {
                    mLogView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        });
    }

    /**
     * Обновляем лог
     *
     * @param message сообщение в UI
     */
    private void updateUI(String tag, String message) {
        mLogItems.add(new LogItem(tag, message));
        // Т.к. таск выполняется не в UI потоке, нам нужно прокинуть событие в UI
        mLogView.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 5) {
                    mLogView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        });
    }

    /**
     * Нахождение совпадений.
     *
     * @see Action
     */
    private void replaceOccurrences() {
        try {
            updateUI("f", "ApkToolPatcher v. " + requireContext()
                    .getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        updateUI("f", "Melissa Framework v. 0.7.0");
        updateUI("f", String.format(Locale.ENGLISH, "Apply %d patches = %s to %s",
                mActionIds.length, Arrays.toString(mActionIds), mProjectDir.getPath()));
        // Старт подготовки
        long start = System.currentTimeMillis();
        // Сюда складывем наши таски
        final ArrayList<Action<Integer>> workerActions = new ArrayList<>();
        // В цикле находим наши таски по их ID
        for (Action<Integer> pendingAction : AsyncRepository.getInstance().findActionsByIds(mActionIds)) {
            // Присваиваем входной параметр
            pendingAction.setArguments(mProjectDir.getPath(), mBaseParam);
            // Слушатель выполнения
            pendingAction.setListener(this);
            // Добавляем модифицированный таск в наш ArrayList
            workerActions.add(pendingAction);
            updateUI("w", String.format(Locale.ENGLISH,
                    "Preparing action \"%s\" for execution with parameters = %s",
                    pendingAction.id(), Arrays.toString(pendingAction.getArguments())));
        }
        // Конец
        long end = System.currentTimeMillis();
        // Нужен для того, чтобы запускать выполнение тасков не сразу, а когда все приготовления
        // будут сделаны
        Handler wait = new Handler();
        wait.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Теперь можно запускать таймер
                mChronometer.start();
                try {
                    Async.series(workerActions).subscribe(new Subscription() {
                        @Override
                        public void result(@NonNull Result result) {
                            // Ловим результат для каджого таска
                            for (Action<?> action : result) {
                                Object anotherResult = action.getResult();
                                //NotificationHelper.show(action, Integer.parseInt(anotherResult.toString()));
                                Log.i("ApkToolPatcher", String.format("Exec action '%s'; returned: %s",
                                        action.id(), anotherResult));
                            }
                            if (mListener != null) {
                                // Сигнал активити, что работа завершена, и можно показать
                                // диалог доната
//								if (this.codepremium2.MainFragment.contains(edoc)) {
//					}else{
                                mListener.onJobFinished();
                            }
                            // Остановка таймера
                            mChronometer.stop();
                        }
                    });
                } catch (Exception e) {
                    if (mListener != null) {
                        mListener.onError(e);
                    }
                }
            }
        }, (end - start) + 2000);
    }

    /**
     * @see OnExecutionListener#onProgressUpdate(Action, int)
     */
    @Override
    public void onProgressUpdate(Action action, int progress) {
        updateUI(action, progress);
    }

    @Override
    public void onEvent(String message) {
        updateUI("i", message);
    }

    @Override
    public void onExecutionError(String error) {
        updateUI("e", error);
    }
}
