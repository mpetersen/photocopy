# Makefile to create a symbolic link to the run script.
#
# Preconditions:
# 1. The makefile is executed in the root directory of the project
# 2. The run script is in the project directory /bin and has the name run.sh
# 3. The symbolic link will have the same name as the project directory
#
# Caution:
# - No checks are performed when creating or removing the link
# - If the link already exists, but points to a different target, the link is overwritten
# - If the link exists it is removed, even if it points to a different target

proj = $(shell pwd)
name = $(shell basename $(proj))

all: build install

install:
	sudo ln -sF $(proj)/bin/run.sh /usr/local/bin/$(name)
	sudo mv -n target/*.app /Applications

build:
	mvn clean install
	sudo codesign --force --deep --sign - target/*.app

uninstall:
	sudo unlink /usr/local/bin/$(name)
