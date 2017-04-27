
package ru.surfproject.app.weather.model.response.city;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Prediction {

    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("matched_substrings")
    @Expose
    public List<MatchedSubstring> matchedSubstrings = null;
    @SerializedName("place_id")
    @Expose
    public String placeId;
    @SerializedName("reference")
    @Expose
    public String reference;
    @SerializedName("structured_formatting")
    @Expose
    public StructuredFormatting structuredFormatting;
    @SerializedName("terms")
    @Expose
    public List<Term> terms = null;
    @SerializedName("types")
    @Expose
    public List<String> types = null;

}
