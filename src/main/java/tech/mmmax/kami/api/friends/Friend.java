package tech.mmmax.kami.api.friends;

import java.util.Objects;

public class Friend {

    String ign;

    public Friend(String ign) {
        this.ign = ign;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Friend friend = (Friend) o;

            return this.ign.equals(friend.ign);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[] { this.ign});
    }

    public String toString() {
        return this.ign;
    }
}
