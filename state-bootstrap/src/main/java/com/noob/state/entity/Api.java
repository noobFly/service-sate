package com.noob.state.entity;

import com.noob.state.constants.Symbol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Api extends Meta {

	private String type;
	private String providerCode;

	public Api(String id, String code, String type, String name, String remark, String providerCode) {
		super(id, code, name, remark);
		this.type = type;
		this.providerCode = providerCode;

	}

	public String getNode() {
		return String.join(Symbol.MIDDLE_LINE, super.getNode(), this.getType());
	}

}
