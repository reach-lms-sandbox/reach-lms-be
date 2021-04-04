# Reach LMS Backend — Java | Spring Boot | REST

## Table of Contents

- [Introduction](#introduction)

- [RESTful Design](#RESTful-design)
    - [HATEOAS](#hateoas)

<a name="introduction"></a>

## Introduction

Reach LMS is an open-source learning management system designed for the developing world. Reach lets organizations offer
education and training to anyone—whether they're working from a laptop in a city center or a solar-charged flip phone in
a remote village.

This repository contains the source code for the RESTful Java Spring Boot backend application that drives the creation,
storage, and access of data that drive this product.

### What does it do? This backend can...

- Authenticate users with Okta, Spring Security, and JWTs
- Authorize resources dependent on the Roles, Scopes, and/or Okta Groups that the user belongs to
- Send resources through various endpoints
- Attach relational links to resources based on (1.) who requested it and (2.) what the resource is

### What do we value? This backend strives to...

- Maintain

This Java Spring REST API application will provide endpoints for users to read various data sets contained in the
application's database. This application will also form the basis for a user authentication with okta and resource
authorization to allow only specific featuresets depending on the user role.

## Database Design

This is database schema which included users, user emails, user roles, program, course, module, admin, student, teacher
models.

The table layout is similar to the common @ManyToMany annotation but with the following exceptions:

* Join tables such as userroles, studentcourses, teachercourses is explicitly created. This allows us to add additional
  columns to the join table
* Since we are creating the join table ourselves, the Many to Many relationship that formed the join table is now two
  Many to One relationships
* All tables now have audit fields (CREATED BY, CREATED DATE, LASTMODIFIED BY, LASTMODIFIED DATE)

The table layout is as follows

* User is the driving table.
* Programs have a Many-To-One relationship with User. Each User (ADMIN) has many user programs combinations. Each user
  program combination has only one User (ADMIN).
* Roles have a Many-To-Many relationship with Users.

---

* Student is the driving table.
* Courses have Many-To-Many relationship with Student

---

* Program is the driving table.
* Courses have Many-To-One realtionship with Program. Each Program have many admin courses combinations. Each program
  courses combination has only one program.

---

* Course is the driving table.
* Students have Many-To-Many relationship with Courses.
* Teachers have Many-To-Many realtionship with Courses

---

<a name="RESTful-design"></a>

## RESTful Design

<a name="hateoas"></a>

### HATEOAS

