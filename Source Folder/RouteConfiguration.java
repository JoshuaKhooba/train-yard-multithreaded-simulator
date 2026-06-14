/*
Name: Joshua Khooba
Course: CNT 4714 Spring 2026
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 15, 2026
Class: RouteConfiguration
Description: Represents a valid route configuration through the yard, specifying the
inbound track, outbound track, and the three switches required in order.
*/

public class RouteConfiguration {
    private int inboundTrack;
    private int switch1;
    private int switch2;
    private int switch3;
    private int outboundTrack;
    
    public RouteConfiguration(int inboundTrack, int switch1, int switch2, int switch3, int outboundTrack) {
        this.inboundTrack = inboundTrack;
        this.switch1 = switch1;
        this.switch2 = switch2;
        this.switch3 = switch3;
        this.outboundTrack = outboundTrack;
    }
    
    public int getInboundTrack() {
        return inboundTrack;
    }
    
    public int getSwitch1() {
        return switch1;
    }
    
    public int getSwitch2() {
        return switch2;
    }
    
    public int getSwitch3() {
        return switch3;
    }
    
    public int getOutboundTrack() {
        return outboundTrack;
    }
    
    public boolean matches(int inbound, int outbound) {
        return this.inboundTrack == inbound && this.outboundTrack == outbound;
    }
}
