package com.walkgis.tiles.util.gdtypes;

import java.util.List;

public class gd_types {
    class GdalFormat {
        private String name;
        private String attribute;
        private String description;
        private boolean canRead;
        private boolean canWrite;
        private boolean canUpdate;
        private boolean hasVirtualIO;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isCanRead() {
            return canRead;
        }

        public void setCanRead(boolean canRead) {
            this.canRead = canRead;
        }

        public boolean isCanWrite() {
            return canWrite;
        }

        public void setCanWrite(boolean canWrite) {
            this.canWrite = canWrite;
        }

        public boolean isCanUpdate() {
            return canUpdate;
        }

        public void setCanUpdate(boolean canUpdate) {
            this.canUpdate = canUpdate;
        }

        public boolean isHasVirtualIO() {
            return hasVirtualIO;
        }

        public void setHasVirtualIO(boolean hasVirtualIO) {
            this.hasVirtualIO = hasVirtualIO;
        }
    }

    class Enums {
        public Enums(List<String> fieldNames) {
            this.fieldNames = fieldNames;
        }

        private List<String> fieldNames;

        public List<String> getFieldNames() {
            return fieldNames;
        }

        public void setFieldNames(List<String> fieldNames) {
            this.fieldNames = fieldNames;
        }
    }
}
