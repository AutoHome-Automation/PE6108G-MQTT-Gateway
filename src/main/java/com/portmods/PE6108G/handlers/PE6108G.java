package com.portmods.PE6108G.handlers;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PE6108G {

    private CommunityTarget target;
    private Snmp snmp;

    List<OID> socketList;

    public PE6108G(String ip) throws IOException {
        init(ip, "writeadmin");
    }

    public enum OutletState {
        unknown(-1),
        off(1),
        on(2);

        private int stateNumber;

        OutletState(int i) {
            stateNumber = i;
        }

        public static OutletState Decode(int toInt) {
            switch (toInt){
                case 1 : return off;
                case 2 : return on;
                default: return unknown;
            }
        }

        public int getStateNumber(){
            return stateNumber;
        }
    }

    private void init(String ip, String writeCommunity) throws IOException {

        target = new CommunityTarget();
        target.setCommunity(new OctetString(writeCommunity));
        target.setAddress(GenericAddress.parse("udp:" + ip + "/161")); // supply your own IP and port
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);

        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();

        setupPDUList();

    }

    private void setupPDUList() {

        socketList = new ArrayList<OID>();
        socketList.add(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.2.2.0"));
        socketList.add(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.2.3.0"));
        socketList.add(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.2.4.0"));
        socketList.add(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.2.5.0"));
        socketList.add(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.2.6.0"));
        socketList.add(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.2.7.0"));
        socketList.add(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.2.8.0"));
        socketList.add(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.2.9.0"));

    }

    public ResponseEvent setOutlet(int outlentNumber, OutletState state) throws IOException {

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(socketList.get(outlentNumber - 1)), new Integer32(state.stateNumber)));
        pdu.setType(PDU.SET);

        return snmp.send(pdu, target);
    }

    public OutletState getOutlet(int outlentNumber) throws IOException {

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(socketList.get(outlentNumber - 1))));
        pdu.setType(PDU.GET);

        ResponseEvent responseEvent = snmp.send(pdu, target);

        PDU response = responseEvent.getResponse();
        if(response == null){

            return OutletState.unknown;

        }

        return OutletState.Decode(response.get(0).getVariable().toInt());
    }

    public String getPower() throws IOException {

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.1.3.1.4")));
        pdu.setType(PDU.GETNEXT);

        ResponseEvent responseEvent = snmp.send(pdu, target);
        PDU response = responseEvent.getResponse();

        return response.get(0).getVariable().toString();
    }

    public String getVoltage() throws IOException {

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.1.3.1.3")));
        pdu.setType(PDU.GETNEXT);

        ResponseEvent responseEvent = snmp.send(pdu, target);
        PDU response = responseEvent.getResponse();

        return response.get(0).getVariable().toString();
    }

    public String getCurrent() throws IOException {

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(".1.3.6.1.4.1.21317.1.3.2.2.2.1.3.1.2")));
        pdu.setType(PDU.GETNEXT);

        ResponseEvent responseEvent = snmp.send(pdu, target);
        PDU response = responseEvent.getResponse();

        return response.get(0).getVariable().toString();
    }

}
