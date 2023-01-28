package com.driver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {
	
	
    private Map<Group, List<User>> groupUserMap;
    private Map<Group, List<Message>> groupMessageMap;
    private Map<Message, User> senderMap;
    private Map<Group, User> adminMap;
    private Set<String> userMobile;
    private int customGroupCount;
    private int messageId;
    
    
    
    
    public WhatsappRepository() {
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    
    
    
    public String createUser(String name ,String mobile)throws Exception
    {
      User user = new User();

      if(userMobile.contains(mobile))
      {
    	  throw new Exception("User already exists");  
      }
      else {
    	   user.setName(name);
    	   user.setMobile(mobile);  
    	   userMobile.add(mobile);
    	   }
    	return "SUCCESS";
    }
    
    
    public Group createGroup(List<User> users){
        // The list contains at least 2 users where the first user is the admin. A group has exactly one admin.
        // If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
        // If there are 2+ users, the name of group should be "Group count". For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
        // Note that a personal chat is not considered a group and the count is not updated for personal chats.
        // If group is successfully created, return group.

        //For example: Consider userList1 = {Alex, Bob, Charlie}, userList2 = {Dan, Evan}, userList3 = {Felix, Graham, Hugh}.
        //If createGroup is called for these userLists in the same order, their group names would be "Group 1", "Evan", and "Group 2" respectively.

    	Group group=new Group();
    	
    	if(users.size()==2)
    	{
    		String name =users.get(1).getName();
    		group.setName(name);
    		group.setNumberOfParticipants(users.size());
    		 
    		
    	 groupUserMap.put(group, users);
    	 customGroupCount +=1;
    	 
    	}
    	else if(users.size()>2)
    	{
    	  String name =	"Group "+ customGroupCount;
    		group.setName(name);
    		group.setNumberOfParticipants(users.size());
    	  groupUserMap.put(group, users);
     	 customGroupCount +=1;
    	}
    	
    	return group;
    }
    
    public int createMessage(String content){
        // The 'i^th' created message has message id 'i'.
        // Return the message id.
    	
    	return messageId;
    	
    	
    }
    
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
    	 
    	if(!groupUserMap.containsKey(group))
    	{
    		throw new Exception("Group does not exist");
    		
    	}
    	
    	
    	List<Message> messageList = new ArrayList();
    	 if(groupUserMap.containsKey(group))
    	{
    	   List<User> userList = groupUserMap.get(group);
    	   if(! userList.contains(sender))
    	   {
    		  throw new Exception("You are not allowed to send message") ;
    	   }
    	   else {
    		   
    		 messageList =  groupMessageMap.get(group);
    		   groupMessageMap.put(group, messageList);
    		   
    	   }
    	  
    	}

    	 return  messageList.size();
    }
    
    
    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if (!adminMap.containsKey(group)) throw new Exception("Group does not exist");
        if (!adminMap.get(group).equals(approver)) throw new Exception("Approver does not have rights");
        List<User> participants = groupUserMap.get(group);
        Boolean userFound = false;
        for (User participant : participants) {
            if (participant.equals(user)) {
                userFound = true;
                break;
            }
        }
        if (userFound) {
            adminMap.put(group, user);
            return "SUCCESS";
        }
        throw new Exception("User is not a participant");

    }
    
    
    public int removeUser(User user) throws Exception {
        Boolean userFound = false;
        Group userGroup = null;
        for (Group group : groupUserMap.keySet()) {
            List<User> participants = groupUserMap.get(group);
            for (User participant : participants) {
                if (participant.equals(user)) {
                    if (adminMap.get(group).equals(user)) {
                        throw new Exception("Cannot remove admin");
                    }
                    userGroup = group;
                    userFound = true;
                    break;
                }
            }
            if (userFound) {
                break;
            }
        }
        if (userFound) {
            List<User> users = groupUserMap.get(userGroup);
            List<User> updatedUsers = new ArrayList<>();
            for (User participant : users) {
                if (participant.equals(user))
                    continue;
                updatedUsers.add(participant);
            }
            groupUserMap.put(userGroup, updatedUsers);

            List<Message> messages = groupMessageMap.get(userGroup);
            List<Message> updatedMessages = new ArrayList<>();
            for (Message message : messages) {
                if (senderMap.get(message).equals(user))
                    continue;
                updatedMessages.add(message);
            }
            groupMessageMap.put(userGroup, updatedMessages);

            HashMap<Message, User> updatedSenderMap = new HashMap<>();
            for (Message message : senderMap.keySet()) {
                if (senderMap.get(message).equals(user))
                    continue;
                updatedSenderMap.put(message, senderMap.get(message));
            }
            senderMap = updatedSenderMap;
            return updatedUsers.size() + updatedMessages.size() + updatedSenderMap.size();
        }
        throw new Exception("User not found");
    }

    public String findMessage(Date start, Date end, int K) throws Exception {
        List<Message> messages = new ArrayList<>();
        for (Group group : groupMessageMap.keySet()) {
            messages.addAll(groupMessageMap.get(group));
        }
        List<Message> filteredMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getTimestamp().after(start) && message.getTimestamp().before(end)) {
                filteredMessages.add(message);
            }
        }
        if (filteredMessages.size() < K) {
            throw new Exception("K is greater than the number of messages");
        }
        Collections.sort(filteredMessages, new Comparator<Message>() {
            public int compare(Message m1, Message m2) {
                return m2.getTimestamp().compareTo(m1.getTimestamp());
            }
        });
        return filteredMessages.get(K - 1).getContent();
    }
    
    

}
