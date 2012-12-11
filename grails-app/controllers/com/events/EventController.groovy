package com.events



import net.fortuna.ical4j.model.Component
import org.springframework.dao.DataIntegrityViolationException

class EventController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [eventInstanceList: Event.list(params), eventInstanceTotal: Event.count()]
    }

    def create() {
        [eventInstance: new Event(params)]
    }

    def save() {
        def eventInstance = new Event(params)
        if (!eventInstance.save(flush: true)) {
            render(view: "create", model: [eventInstance: eventInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'event.label', default: 'Event'), eventInstance.id])
        redirect(action: "show", id: eventInstance.id)
    }

    def show(Long id) {
        def eventInstance = Event.get(id)
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), id])
            redirect(action: "list")
            return
        }

        [eventInstance: eventInstance]
    }

    def edit(Long id) {
        def eventInstance = Event.get(id)
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), id])
            redirect(action: "list")
            return
        }

        [eventInstance: eventInstance]
    }

    def update(Long id, Long version) {
        def eventInstance = Event.get(id)
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (eventInstance.version > version) {
                eventInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'event.label', default: 'Event')] as Object[],
                          "Another user has updated this Event while you were editing")
                render(view: "edit", model: [eventInstance: eventInstance])
                return
            }
        }

        eventInstance.properties = params

        if (!eventInstance.save(flush: true)) {
            render(view: "edit", model: [eventInstance: eventInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'event.label', default: 'Event'), eventInstance.id])
        redirect(action: "show", id: eventInstance.id)
    }

    def delete(Long id) {
        def eventInstance = Event.get(id)
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), id])
            redirect(action: "list")
            return
        }

        try {
            eventInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'event.label', default: 'Event'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'event.label', default: 'Event'), id])
            redirect(action: "show", id: id)
        }
    }
	
	
	
	def EventService;
	def populateDB() {
		net.fortuna.ical4j.model.Calendar calendar = EventService.buildCalendar("en.usa#holiday@group.v.calendar.google.com");
		for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			// Only create an event item if the current component is of VEVENT and NOT VTIMEZONE
			if (component.getName().equals("VEVENT")) {
//				System.out.println("Component [" + component.getName() + "]");
				String name = EventService.getName(component);
				String summary = EventService.getName(component);
				Date startDate = EventService.getStartTime(component);
				Date endDate = EventService.getStartTime(component);
				println(name);
				def eventInstance = new Event(
					name: name,
					description: summary,
					startDate: startDate,
					endDate: endDate
				);
				if (!eventInstance.save(flush: true)) {
					render(view: "create", model: [eventInstance: eventInstance])
					return
				}
			}

		}
		redirect(action: "list");
//		EventService.getEvents("en.usa#holiday@group.v.calendar.google.com");
		
	}
	
}
