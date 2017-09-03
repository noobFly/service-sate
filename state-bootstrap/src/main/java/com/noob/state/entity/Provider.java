package com.noob.state.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Provider extends Meta {
	private List<Service> serviceList;

	public Provider(String code, String name,List<Service> serviceList) {
		super(code, name);
		this.serviceList = serviceList;
	}

}
