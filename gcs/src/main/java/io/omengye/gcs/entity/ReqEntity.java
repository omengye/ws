package io.omengye.gcs.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.omengye.common.utils.Utils;
import io.omengye.gcs.valid.CollectionValid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReqEntity {

    @NotEmpty
    @Size(max = 100)
    private String q;

    @NotNull
    @Max(101)
    private Integer start;

    @NotNull
    @Max(11)
    private Integer num;

    /**
     * sort by date
     */
    @CollectionValid(vals = {"date"}, message = "sort is not valid", acceptNull = true)
    private String sort;

    /**
     * language: lang_zh-CN, lang_zh-TW, lang_en
     */
    @CollectionValid(vals = {"lang_zh-CN", "lang_zh-TW", "lang_en"}, message = "lr is not valid", acceptNull = true)
    private String lr;

    /**
     *  1 day
     *  1 week
     *  1 month
     *  1 year
     */
    @CollectionValid(vals = {"d1", "w1", "m1", "y1"}, message = "dateRestrict is not valid", acceptNull = true)
    private String dateRestrict;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("&q=").append(Utils.filterQuery(q, new String[]{"{","}","#","\\","/"}))
                .append("&start=").append(start)
                .append("&num=").append(num);
        if (Utils.isNotEmpty(sort)) {
            sb.append("&sort=").append(sort);
        }
        if (Utils.isNotEmpty(lr)) {
            sb.append("&lr=").append(lr);
        }
        if (Utils.isNotEmpty(dateRestrict)) {
            sb.append("&dateRestrict=").append(dateRestrict);
        }

        return sb.toString();
    }


}
