package com.lambdaschool.oktafoundation.services;


import com.lambdaschool.oktafoundation.exceptions.ProgramNotFoundException;
import com.lambdaschool.oktafoundation.exceptions.UserNotFoundException;
import com.lambdaschool.oktafoundation.models.*;
import com.lambdaschool.oktafoundation.repository.ProgramRepository;
import com.lambdaschool.oktafoundation.repository.TagRepository;
import com.lambdaschool.oktafoundation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Transactional
@Service(value = "programService")
public class ProgramServiceImpl
		implements ProgramService {

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	UserRepository userrepos;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	TagService tagService;

	@Override
	public Program save(
			long userid,
			Program program
	)
	throws ProgramNotFoundException, UserNotFoundException {
		Program newProgram = new Program();
		if (program.getProgramId() != 0) {
			findProgramsById(program.getProgramId()); // throws if program not found
			newProgram.setProgramId(program.getProgramId());
		}
		newProgram.setProgramName(program.getProgramName());
		newProgram.setProgramType(program.getProgramType());
		newProgram.setProgramDescription(program.getProgramDescription());
		for (ProgramTags programTags : program.getTags()) {
			newProgram.addTag(programTags.getTag());
		}
		newProgram.getCourses()
				.clear();
		for (Course course : program.getCourses()) {
			newProgram.getCourses()
					.add(new Course(course.getCourseName(),
							course.getCourseDescription(),
							course.getCourseDescription(),
							newProgram
					));
		}
		User currentUser = userrepos.findById(userid)
				.orElseThrow(() -> new UserNotFoundException(userid));
		newProgram.setUser(currentUser);
		return programRepository.save(newProgram);
	}

	@Override
	public Program save(
			long userid,
			ProgramIn programIn
	)
	throws ProgramNotFoundException, UserNotFoundException {
		Program newProgram = programIn.toProgram();
		for (Tag tag : programIn.getTags()) {
			Optional<Tag> optional = tagService.find(tag.getTitle());
			if (optional.isEmpty()) {
				newProgram.addTag(tag);
			} else if (!newProgram.containsTag(optional.get())) {
				newProgram.addTag(optional.get());
			}
		}
		return save(userid, newProgram);
	}

	@Override
	public List<Program> findProgramsByTagName(String title) {
		List<Program> programs = new ArrayList<>();
		programRepository.findByTags_tag_titleLikeIgnoreCase(title)
				.iterator()
				.forEachRemaining(programs::add);
		return programs;
	}

	@Override
	public List<Program> findAll() {
		List<Program> programs = new ArrayList<>();
		programRepository.findAll()
				.iterator()
				.forEachRemaining(programs::add);
		return programs;
	}

	@Override
	public Program findProgramsById(long id)
	throws ProgramNotFoundException {
		return programRepository.findById(id)
				.orElseThrow(() -> new ProgramNotFoundException(id));
	}

	@Override
	public Program findProgramsByName(String name)
	throws ProgramNotFoundException {
		return programRepository.findByProgramNameIgnoreCase(name)
				.orElseThrow(() -> new ProgramNotFoundException(name));
	}

	@Override
	public void delete(long id)
	throws ProgramNotFoundException {
		findProgramsById(id); // throws if not found
		programRepository.deleteById(id);
	}

	@Override
	public Program update(
			ProgramIn programIn,
			long programId
	) {
		Program existingProgram = findProgramsById(programId);
		for (Tag tag : programIn.getTags()) {
			Optional<Tag> optional;
			if (tag.getTagId() != 0) {
				optional = tagService.find(tag.getTagId());
			} else {
				optional = tagService.find(tag.getTitle());
			}
			if (optional.isEmpty()) {
				existingProgram.addTag(tag);
			} else {
				Tag existingTag = optional.get();
				if (tag.getTitle() != null) {
					existingTag.setTitle(tag.getTitle());
				}
				if (tag.getHexCode() != null) {
					existingTag.setHexCode(tag.getHexCode());
				}
				if (!existingProgram.containsTag(existingTag)) {
					existingProgram.addTag(existingTag);
				}
			}
		}
		Program newProgram = programIn.toProgram(existingProgram);
		return update(newProgram, programId);
	}

	@Override
	public Program update(
			Program program,
			long id
	)
	throws ProgramNotFoundException {
		Program oldProgram = findProgramsById(id); // throws if not found
		if (program.getProgramName() != null) {
			oldProgram.setProgramName(program.getProgramName());
		}
		if (program.getProgramType() != null) {
			oldProgram.setProgramType(program.getProgramType());
		}
		if (program.getProgramDescription() != null) {
			oldProgram.setProgramDescription(program.getProgramDescription());
		}
		if (program.getTags()
				    .size() > 0) {
			for (ProgramTags programTags : program.getTags()) {
				oldProgram.addTag(programTags.getTag());
			}
		}
		return programRepository.save(oldProgram);
	}

}
