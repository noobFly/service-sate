package com.noob.state.entity;

import com.noob.state.constants.Symbol;
import com.noob.state.utils.GsonUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Meta {	
	protected String id;
	protected String code;
	protected String name;
	protected String remark;

	public String toJson() {
		return GsonUtil.toJson(this);
	}

	public String getNode() {
		return String.join(Symbol.MIDDLE_LINE, this.getId(), this.getCode());
	}

}
