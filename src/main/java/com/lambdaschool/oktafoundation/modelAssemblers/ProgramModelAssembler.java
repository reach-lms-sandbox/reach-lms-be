package com.lambdaschool.oktafoundation.modelAssemblers;


import com.lambdaschool.oktafoundation.controllers.CourseController;
import com.lambdaschool.oktafoundation.controllers.ProgramController;
import com.lambdaschool.oktafoundation.models.Program;
import com.lambdaschool.oktafoundation.models.RoleType;
import com.lambdaschool.oktafoundation.services.HelperFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ProgramModelAssembler
		implements RepresentationModelAssembler<Program, EntityModel<Program>> {

	@Autowired
	HelperFunctions helperFunctions;

	@Override
	public EntityModel<Program> toModel(Program program) {
		EntityModel<Program> programEntity =  EntityModel.of(program,
				// Link to SELF --- GET /programs/program/{programid}
				linkTo(methodOn(ProgramController.class).getProgramById(program.getProgramid())).withSelfRel(),
				// Link to self_by_name --- GET /programs/program/{programname}
				linkTo(methodOn(ProgramController.class).getProgramByName(program.getProgramname())).withRel("self_by_name"),
				// Link to associated courses
				linkTo(methodOn(CourseController.class).getCoursesByProgramid(program.getProgramid())).withRel("courses")
		);

		RoleType currentRole = helperFunctions.getCurrentPriorityRole();

		if (currentRole == RoleType.ADMIN) {
			programEntity.add(
					// Link to `GET all programs by this program's admin`
					linkTo(methodOn(ProgramController.class).getProgramsByUserId(program.getUser()
							.getUserid())).withRel("admin_programs"),
					// Link to `GET all programs`
					linkTo(methodOn(ProgramController.class).listAllPrograms()).withRel("all_programs")
			);
		}

		return programEntity;
	}

}
