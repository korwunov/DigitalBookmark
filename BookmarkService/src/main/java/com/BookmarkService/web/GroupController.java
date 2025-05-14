package com.BookmarkService.web;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Group;
import com.BookmarkService.middleware.Authentication;
import com.BookmarkService.services.GroupService;
import com.BookmarkService.web.dto.GroupAssignmentDto;
import com.BookmarkService.web.dto.GroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api/bookmark/groups")
@ResponseBody
public class GroupController {
    public GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public Group createGroup(@RequestHeader("Authorization") String token, Object user, @RequestBody GroupDTO groupDto) {
        return groupService.createGroup(groupDto.getName());
    }

    @GetMapping
    @Authentication(roles = {EROLE.ROLE_TEACHER, EROLE.ROLE_STUDENT})
    public List<Group> getGroups(@RequestHeader("Authorization") String token, Object user) {
        return groupService.getAllGroups();
    }

    @PostMapping("/assign")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public void assignGroupToStudent(@RequestHeader("Authorization") String token, Object user, @RequestBody GroupAssignmentDto assignmentDto) {
        groupService.assignGroupToStudent(assignmentDto.getStudentId(), assignmentDto.getGroupId());
    }

    @PostMapping("/unassign/{studentId}")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public void unassignGroupToStudent(@RequestHeader("Authorization") String token, Object user, @PathVariable Long studentId) {
        groupService.unassignGroupToStudent(studentId);
    }

    @DeleteMapping("/{id}")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public void deleteGroup(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        groupService.deleteGroup(id);
    }
}
