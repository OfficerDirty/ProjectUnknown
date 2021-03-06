package Model.Abstraction;

import Model.Event.IEventHandler;
import Model.Event.InteractionEvent;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A base class for objects that manage {@link IEventHandler}s. When overriding any method from this class, super must be called.
 */
public abstract class AbstractEventInteractionObject implements IEventInteractableObject {

    private Map<EventType, List<IEventHandler>> eventHandlerMapping;

    /**
     * Constructs a new {@link AbstractEventInteractionObject} object
     */
    protected  AbstractEventInteractionObject(){
        eventHandlerMapping = new HashMap<>();
        for(EventType t : EventType.values()){
            eventHandlerMapping.put(t, new ArrayList<>());
        }
    }

    @Override
    public void keyPressed(int key) {
        fireEvent(EventType.KEY_PRESSED, new InteractionEvent(key, null, this));
    }

    @Override
    public void keyReleased(int key){
        fireEvent(EventType.KEY_RELEASED, new InteractionEvent(key, null, this));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        fireEvent(EventType.MOUSE_CLICKED, new InteractionEvent(-1, e, this));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        fireEvent(EventType.MOUSE_RELEASED, new InteractionEvent(-1, e, this));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        fireEvent(EventType.MOUSE_PRESSED, new InteractionEvent(-1, e, this));
    }

    @Override
    public void addEventHandler(EventType t, IEventHandler handler) {
        eventHandlerMapping.get(t).add(handler);
    }

    @Override
    public void removeEventHandler(EventType t, IEventHandler handler) {
        eventHandlerMapping.get(t).remove(handler);
    }

    protected void fireEvent(EventType t, InteractionEvent eventObject){
        for(IEventHandler handler : eventHandlerMapping.get(t)){
            handler.onEvent(eventObject);
        }
    }
}
