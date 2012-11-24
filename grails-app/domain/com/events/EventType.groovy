package com.events

public enum EventType {
	MISC(0),
	ACADEMICS(1),
	ARCHITECTURE(2),
	ART(3),
	CHARITY(4),
	CINEMA(5),
	FOOD(6),
	LITERATURE(7),
	MUSIC(8),
	NIGHTLIFE(9),
	SPORTS(10),
	THEATER(11);

	final int id;

	private EventType(int id) {
		this.id = id;
	}

	static EventType byId(int id) {
		values().find{ it.id == id }
	}
}
