package ru.svolf.melissa.swipeback;

/**
 * Этот интерфейс нужен для общения Activity с ViewPager и фрагментами внутри нее.
 * С помощью него мы узнаем, какая вкладка выбрана в данный момент, и, в зависимости
 * от этого, изменяем чувствительность SwipeBack у Activity, чтоб при свайпах между вкладками
 * она случайно не закрылась.
 *
 * @see apk.tool.patcher.ui.settings.SettingsHostFragment
 * @see apk.tool.patcher.ui.settings.SettingsActivity
 */
public interface OnTabSwipeListener {
    /**
     * Вызывается при изменении состояния ViewPager
     * @param tabNumber номер текущей выбранной вкладки
     */
    void onTabSwipe(int tabNumber);
}
