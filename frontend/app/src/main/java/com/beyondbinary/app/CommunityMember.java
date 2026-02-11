package com.beyondbinary.app;

public class CommunityMember {
    private String name;
    private String lowSocial;
    private String lowPhysical;
    private String profileImageUrl;
    private boolean isInvited;

    public CommunityMember(String name, String lowSocial, String lowPhysical, String profileImageUrl) {
        this.name = name;
        this.lowSocial = lowSocial;
        this.lowPhysical = lowPhysical;
        this.profileImageUrl = profileImageUrl;
        this.isInvited = false;
    }

    public String getName() {
        return name;
    }

    public String getLowSocial() {
        return lowSocial;
    }

    public String getLowPhysical() {
        return lowPhysical;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public boolean isInvited() {
        return isInvited;
    }

    public void setInvited(boolean invited) {
        isInvited = invited;
    }

    public boolean hasLowSocial() {
        return lowSocial != null && !lowSocial.isEmpty();
    }

    public boolean hasLowPhysical() {
        return lowPhysical != null && !lowPhysical.isEmpty();
    }
}
