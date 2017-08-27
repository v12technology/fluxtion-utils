/* 
 * Copyright (C) 2017 V12 Technology Limited (greg.higgins@v12technology.com)
 *
 * This file is part of Fluxtion.
 *
 * Fluxtion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.runtime.plugin.auditing;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import java.util.HashSet;
import java.util.Set;

/**
 * Delegating auditor, allows Auditors to be registered at run time if a 
 * DelegatingAuditor is built into the generated SEP.
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
