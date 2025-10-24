package com.habittracker.controller;

import com.habittracker.dto.HabitDTO;
import com.habittracker.model.Habit;
import com.habittracker.model.HabitCompletion;
import com.habittracker.model.User;
import com.habittracker.service.HabitService;
import com.habittracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/habits")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HabitController {
    
    @Autowired
    private HabitService habitService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<Habit>> getUserHabits(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Habit> habits = habitService.findActiveHabitsByUserId(user.getId());
        return ResponseEntity.ok(habits);
    }
    
    @PostMapping
    public ResponseEntity<Habit> createHabit(@Valid @RequestBody HabitDTO habitDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        Habit habit = new Habit();
        habit.setName(habitDTO.getName());
        habit.setDescription(habitDTO.getDescription());
        habit.setType(habitDTO.getType());
        habit.setFrequency(habitDTO.getFrequency());
        habit.setTargetValue(habitDTO.getTargetValue());
        habit.setTargetUnit(habitDTO.getTargetUnit());
        habit.setUser(user);
        
        Habit createdHabit = habitService.createHabit(habit);
        return ResponseEntity.ok(createdHabit);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabit(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Optional<Habit> habit = habitService.findById(id);
        
        if (habit.isEmpty() || !habit.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(habit.get());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Habit> updateHabit(@PathVariable Long id, @Valid @RequestBody HabitDTO habitDTO, 
                                         Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Optional<Habit> habitOpt = habitService.findById(id);
        
        if (habitOpt.isEmpty() || !habitOpt.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        Habit habit = habitOpt.get();
        habit.setName(habitDTO.getName());
        habit.setDescription(habitDTO.getDescription());
        habit.setType(habitDTO.getType());
        habit.setFrequency(habitDTO.getFrequency());
        habit.setTargetValue(habitDTO.getTargetValue());
        habit.setTargetUnit(habitDTO.getTargetUnit());
        
        Habit updatedHabit = habitService.updateHabit(habit);
        return ResponseEntity.ok(updatedHabit);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Optional<Habit> habit = habitService.findById(id);
        
        if (habit.isEmpty() || !habit.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        habitService.deleteHabit(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<HabitCompletion> completeHabit(@PathVariable Long id, 
                                                        @RequestParam(required = false) Integer value,
                                                        @RequestParam(required = false) String notes,
                                                        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Optional<Habit> habit = habitService.findById(id);
        
        if (habit.isEmpty() || !habit.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        HabitCompletion completion = habitService.completeHabit(id, value, notes);
        return ResponseEntity.ok(completion);
    }
    
    @GetMapping("/{id}/completions")
    public ResponseEntity<List<HabitCompletion>> getHabitCompletions(@PathVariable Long id, 
                                                                  Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Optional<Habit> habit = habitService.findById(id);
        
        if (habit.isEmpty() || !habit.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        List<HabitCompletion> completions = habitService.getHabitCompletions(id);
        return ResponseEntity.ok(completions);
    }
}
