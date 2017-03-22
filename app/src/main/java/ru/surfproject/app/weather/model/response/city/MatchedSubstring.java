
package ru.surfproject.app.weather.model.response.city;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatchedSubstring {

    @SerializedName("length")
    @Expose
    public Integer length;
    @SerializedName("offset")
    @Expose
    public Integer offset;

}
