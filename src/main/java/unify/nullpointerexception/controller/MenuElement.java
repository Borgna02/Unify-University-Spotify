package unify.nullpointerexception.controller;

public class MenuElement {

	private String name;
	private String view;

	public MenuElement(String name, String view) {
		super();
		this.name = name;
		this.view = view;
	}

	public String getName() {
		return name;
	}

	public void setName(String nome) {
		this.name = nome;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

}
