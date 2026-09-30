package in.sp.main.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "call_logs")
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Client / Lead
    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    // Employee who made the call
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Incoming / Outgoing
    private String callType;

    // Call result
    private String callStatus;

    // Call date & time
    private LocalDateTime callTime;

    // Notes about conversation
    @Column(length = 1000)
    private String notes;

    public CallLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public LocalDateTime getCallTime() {
        return callTime;
    }

    public void setCallTime(LocalDateTime callTime) {
        this.callTime = callTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}