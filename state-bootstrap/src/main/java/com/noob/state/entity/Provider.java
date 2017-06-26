package com.noob.state.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Provider extends Meta {
	private List<Api> apiList;

	public Provider(String id, String code, String name, String remark, List<Api> apiList) {
		super(id, code, name, remark);
		this.apiList = apiList;
	}

}
