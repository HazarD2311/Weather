
package ru.surfproject.app.weather.model.response.city;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructuredFormatting {

    @SerializedName("main_text")
    @Expose
    public String mainText;
    @SerializedName("main_text_matched_substrings")
    @Expose
    public List<MainTextMatchedSubstring> mainTextMatchedSubstrings = null;
    @SerializedName("secondary_text")
    @Expose
    public String secondaryText;

}
