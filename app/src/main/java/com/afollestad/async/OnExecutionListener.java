package com.afollestad.async;


/**
 * Оповещает о изменениях в бэкграунде
 *
 * @author Artem Zhiganov (SnowVolf)
 */
public interface OnExecutionListener {
    /**
     * Вызывается, когда найдено совпадение с регуляркой
     *
     * @param action   текущий процесс,
     *                 который выполняется
     * @param progress общее число совпадений,
     *                 которые найдены регуляркой
     */
    void onProgressUpdate(Action action, int progress);

    /**
     * Вызывается для оповещения об изменении состояния процессса
     *
     * @param message текст пояснения
     */
    void onEvent(String message);

    /**
     * Вызывается для оповещения UI об ошибках в процессе выполнения
     * @param error ошибка выполнения
     */
    void onExecutionError(String error);
}