package tech.mmmax.kami.api.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import tech.mmmax.kami.api.config.ISavable;
import tech.mmmax.kami.api.friends.Friend;

public class FriendManager implements ISavable {

    public static FriendManager INSTANCE;
    List friends = new ArrayList();

    public FriendManager() {
        SavableManager.INSTANCE.getSavables().add(this);
    }

    public List getFriends() {
        return this.friends;
    }

    public boolean isFriend(Entity entity) {
        Friend testFriend = new Friend(entity.getName());

        return this.friends.contains(testFriend);
    }

    public void addFriend(Entity entity) {
        Friend friend = new Friend(entity.getName());

        this.friends.add(friend);
    }

    public void removeFriend(Entity entity) {
        Friend friend = new Friend(entity.getName());

        this.friends.remove(friend);
    }

    public void load(Map objects) {
        if (objects.get("friends") != null) {
            List friendsList = (List) objects.get("friends");
            Iterator iterator = friendsList.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                this.friends.add(new Friend(s));
            }
        }

    }

    public Map save() {
        HashMap toSave = new HashMap();
        ArrayList friendList = new ArrayList();
        Iterator iterator = this.friends.iterator();

        while (iterator.hasNext()) {
            Friend friend = (Friend) iterator.next();

            friendList.add(friend.toString());
        }

        toSave.put("friends", friendList);
        return toSave;
    }

    public String getFileName() {
        return "friends.yml";
    }

    public String getDirName() {
        return "misc";
    }
}
