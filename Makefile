# Makefile for building, packaging, and signing RetroEdit app
# Extract the version from pom.xml.
# If xmllint isn't available on your system, you could instead use:
# VERSION := $(shell sed -n 's/.*<version>\(.*\)<\/version>.*/\1/p' pom.xml | head -n 1)
# Extract the version from pom.xml using sed (xmllint may not be available on Linux)
VERSION := $(shell sed -n 's/.*<version>\(.*\)<\/version>.*/\1/p' pom.xml | head -n 1)

# Variables
APP_NAME = RetroEdit
MAIN_JAR = retroedit-$(VERSION)-jar-with-dependencies.jar
MAIN_CLASS = com.github.an0nn30.retroedit.Main
ICON_PATH_MAC = src/main/resources/retro_edit_mac_icns.icns
ICON_PATH_WIN= src/main/resources/retro.ico
ICON_PATH_LINUX= src/main/resources/retroedit.png
APP_VERSION = $(VERSION)
VENDOR = "An0nn30"
OUTPUT_DIR = output/
TARGET_DIR = target/
DEV_ID = "Developer ID Application: Dustin McKennaWatts (G8Y2U3KKZR)"
APP_PATH = $(OUTPUT_DIR)$(APP_NAME).app

# Default target
.PHONY: all
all: jar deploy sign

# Build the JAR using Maven
.PHONY: jar
jar:
	mvn clean package

# Create the macOS app using jpackage
.PHONY: deploy
deploy: jar
	rm -rf output/ && \
	jpackage --type app-image \
	  --name $(APP_NAME) \
	  --input $(TARGET_DIR) \
	  --main-jar $(MAIN_JAR) \
	  --main-class $(MAIN_CLASS) \
	  --java-options '--enable-preview' \
	  --icon $(ICON_PATH_MAC) \
	  --app-version $(APP_VERSION) \
	  --vendor $(VENDOR) \
	  --dest $(OUTPUT_DIR) \
	  --verbose

.PHONY: deb
deb: jar
	rm -rf $(OUTPUT_DIR) && \
	jpackage --type deb \
	  --name $(APP_NAME) \
	  --input $(TARGET_DIR) \
	  --main-jar $(MAIN_JAR) \
	  --main-class $(MAIN_CLASS) \
	  --app-version $(APP_VERSION) \
	  --vendor $(VENDOR) \
	  --icon $(ICON_PATH_LINUX) \
	  --linux-shortcut \
	  --resource-dir linux/ \
	  --dest $(OUTPUT_DIR) \
	  --verbose

# Sign the application with Apple's Developer ID
#.PHONY: sign
#sign: deploy
#	codesign --deep --force --verbose \
#	  --sign "$(DEV_ID)" \
#	  $(APP_PATH)

# Create a DMG with the signed .app file
.PHONY: dmg
dmg: deploy
	jpackage --type dmg \
	  --name $(APP_NAME) \
	  --app-image $(APP_PATH) \
	  --icon $(ICON_PATH_MAC) \
	  --app-version $(APP_VERSION) \
	  --vendor $(VENDOR) \
	  --dest $(OUTPUT_DIR) \
	  --verbose

.PHONY: exe
exe:
	jpackage --type exe \
	  --name $(APP_NAME) \
	  --app-image $(APP_PATH) \
	  --icon $(ICON_PATH_WIN) \
	  --app-version $(APP_VERSION)\
	  --vendor $(VENDOR) \
	  --dest $(OUTPUT_DIR) \
	  --verbose




# Clean generated files
.PHONY: clean
clean:
	mvn clean
	rm -rf $(OUTPUT_DIR)

