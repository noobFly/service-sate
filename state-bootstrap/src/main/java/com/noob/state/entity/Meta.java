package com.noob.state.entity;

import com.noob.state.util.GsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Meta {
    protected String code;
    protected String name;

    public String toJson() {
        return GsonUtil.toJson(this);
    }
    public String getNode() {
        return this.getCode();
    }


}
