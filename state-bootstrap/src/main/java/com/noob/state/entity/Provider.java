package com.noob.state.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Provider extends Meta {
	private List<Service> serviceList;

	public Provider(String id, String code, String name, String remark, List<Service> serviceList) {
		super(id, code, name, remark);
		this.serviceList = serviceList;
	}

}
