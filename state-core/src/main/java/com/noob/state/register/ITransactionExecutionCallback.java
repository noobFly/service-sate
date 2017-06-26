package com.noob.state.register;

import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;

public interface ITransactionExecutionCallback {
	void execute(CuratorTransactionFinal curatorTransactionFinal) throws Exception;
}
