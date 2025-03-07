package unify.nullpointerexception.controller;

import java.io.IOException;

import unify.nullpointerexception.business.BusinessException;

public interface DataInitializable<T> {

	/*
	 * Implementato come metodo di default in modo tale che i controllori che
	 * implementano tale interfaccia non sono costretti ad implementare il metodo
	 * qualora non sia necessario
	 */
	default void initializeData(T data) throws IOException, BusinessException{}
	//default void updateUI(T newData){}

}
