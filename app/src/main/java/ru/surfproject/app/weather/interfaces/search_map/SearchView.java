package ru.surfproject.app.weather.interfaces.search_map;

import java.util.List;

public interface SearchView {
    void showProgress(boolean flag);
    void showResult(List<String> listCitys);
    void showError(String error);
}
