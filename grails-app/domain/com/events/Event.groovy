package com.events

class Event {

    String name
    String description
    Date startDate
    Date endDate
    int type

	EventType getEventType() { type ? EventType.byId(type) : EventType.MISC }
	void setEventType(EventType eventType) { type = eventType.id } 

	static transients = ['eventType']

    static constraints = {
		name blank:false, nullable: false
		description blank:false, nullable: false 
		type inList: EventType.values()*.id
    }

	static mapping = {
		type sqlType: 'tinyint'
	}
}
