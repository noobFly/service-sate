package com.noob.state.entity.adapter;

import java.util.List;

import com.google.common.collect.Lists;
import com.noob.state.entity.Meta;
import com.noob.state.monitor.Monitor;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Adapter<T extends Meta> {

	@NonNull
	private T t;
	private List<Monitor> monitorList = Lists.newArrayList();
}
