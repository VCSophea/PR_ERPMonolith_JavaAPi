package com.udaya.service;

import com.udaya.model.Group;
import com.udaya.repository.GroupRepository;
import com.udaya.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final GroupRepository groupRepository;
	private final UserGroupRepository userGroupRepository;

	public List<Group> getAllActiveGroups(String keyword) {
		return groupRepository.findByIsActive(1, keyword);
	}

	public Group getGroupById(Long id) {
		return groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
	}

	public List<Group> getUserGroups(Long userId) {
		return userGroupRepository.findGroupIdsByUserId(userId).stream().map(groupRepository::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}

	@Transactional
	public Group createGroup(Group group) {
		group.setCreated(LocalDateTime.now());
		group.setIsActive(1);
		return groupRepository.save(group);
	}

	@Transactional
	public void updateGroup(Group group) {
		group.setModified(LocalDateTime.now());
		groupRepository.update(group);
	}

	@Transactional
	public void assignUserToGroup(Long userId, Long groupId) {
		userGroupRepository.assignUserToGroup(userId, groupId);
	}

	@Transactional
	public void removeUserFromGroup(Long userId, Long groupId) {
		userGroupRepository.removeUserFromGroup(userId, groupId);
	}

	public List<Long> getGroupMembers(Long groupId) {
		return userGroupRepository.findUserIdsByGroupId(groupId);
	}
}
