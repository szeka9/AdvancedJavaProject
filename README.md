
# Introduction

The purpose of this project is to develop a tool path management application for
various CNC (computer numerical control) machines, such as 3D printers, laser
cutters, and milling machines. In the field of Computer-aided Manufacturing (CAM), a
tool path is a set of instructions that describe the operations to perform on the
machine, such as moving to specific locations or changing tools. Tool paths are
typically written in Gcode, a universally interpretable programming language for
CNC machine controller hardware.


# How to run?

Set up a PostgreSQL database and provide the database URL and credentials in application.yml.
Make Sure to have proper read/write permissions for the "upload" directory, where GCode files
will be stored (uploaded by the client).