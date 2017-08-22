package com.fluxtion.runtime.plugin.auditing;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import java.util.HashSet;
import java.util.Set;

/**
 * Delegating auditor.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class DelegatingAuditor implements Auditor {

    private Set<Auditor> eventAuditors;
    private Set<Auditor> nodeAuditors;
    private Set<Node> nodeRegistrations;

    public static class AuditorRegistration extends Event{

        private final boolean register;
        private final Auditor auditor;

        public AuditorRegistration(boolean register, Auditor auditor) {
            this.register = register;
            this.auditor = auditor;
        }

    }

    private static class Node {

        Object node;
        String name;

        public Node(Object node, String name) {
            this.node = node;
            this.name = name;
        }

    }

    @EventHandler
    public void auditorRegistration(AuditorRegistration auditorRegistration) {
        Auditor auditor = auditorRegistration.auditor;
        if (auditorRegistration.register) {
            auditor.init();
            nodeRegistrations.forEach(n -> auditor.nodeRegistered(n.node, n.name));
            eventAuditors.add(auditor);
            if (auditor.auditInvocations()) {
                nodeAuditors.add(auditor);
            }
        } else {
            eventAuditors.remove(auditor);
            nodeAuditors.remove(auditor);
        }
    }

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        nodeRegistrations.add(new Node(node, nodeName));
    }

    @Override
    public void eventReceived(Event event) {
        eventAuditors.forEach(a -> a.eventReceived(event));
    }

    @Override
    public void eventReceived(Object event) {
        eventAuditors.forEach(a -> a.eventReceived(event));
    }

    @Override
    public void processingComplete() {
        eventAuditors.forEach(Auditor::processingComplete);
    }

    @Override
    public void init() {
        eventAuditors = new HashSet<>();
        nodeAuditors = new HashSet<>();
        nodeRegistrations = new HashSet<>();
    }

    @Override
    public void tearDown() {
        eventAuditors.forEach(Auditor::tearDown);
    }

    @Override
    public void nodeInvoked(Object node, String nodeName, String methodName, Object event) {
        nodeAuditors.forEach(a -> a.nodeInvoked(node, nodeName, methodName, event));
    }

    @Override
    public boolean auditInvocations() {
        return true;
    }

}
