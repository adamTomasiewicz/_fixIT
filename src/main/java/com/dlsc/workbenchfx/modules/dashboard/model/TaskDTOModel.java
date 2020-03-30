package com.dlsc.workbenchfx.modules.dashboard.model;


import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.List;


public class TaskDTOModel {

    private String uuid;
    private String name;
    private String description;
    private int urgency;
    private int importance;
    private String colorCategory;
    private List<String> tags;
    private LocalDateTime createLocalDateTime;
    private List<LocalDateTime> listOfLocalDateTimes;

    public TaskDTOModel(TaskBuilder tb) {
        this.uuid = tb.uuid;
        this.name = name;
        this.description = description;
        this.urgency = urgency;
        this.importance = importance;
        this.colorCategory = colorCategory;
        this.tags = tags;
        this.createLocalDateTime = createLocalDateTime;
        this.listOfLocalDateTimes = listOfLocalDateTimes;
    }

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getUrgency() {
        return urgency;
    }
    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }
    public int getImportance() {
        return importance;
    }
    public void setImportance(int importance) {
        this.importance = importance;
    }
    public String getColorCategory() {
        return colorCategory;
    }
    public void setColorCategory(String colorCategory) {
        this.colorCategory = colorCategory;
    }
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public LocalDateTime getCreateLocalDateTime() {
        return createLocalDateTime;
    }
    public void setCreateLocalDateTime(LocalDateTime createLocalDateTime) {
        this.createLocalDateTime = createLocalDateTime;
    }
    public List<LocalDateTime> getListOfLocalDateTimes() {
        return listOfLocalDateTimes;
    }
    public void setListOfLocalDateTimes(List<LocalDateTime> listOfLocalDateTimes) {
        this.listOfLocalDateTimes = listOfLocalDateTimes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("uuid", uuid)
                .append("name", name)
                .append("description", description)
                .append("urgency", urgency)
                .append("importance", importance)
                .append("colorCategory", colorCategory)
                .append("tags", tags)
                .append("createLocalDateTime", createLocalDateTime)
                .append("listOfLocalDateTimes", listOfLocalDateTimes)
                .toString();
    }

    public static class TaskBuilder {
        private String uuid, name, description, colorCategory;
        private int urgency,importance;
        private List<String> tags;
        private LocalDateTime createLocalDateTime;
        private List<LocalDateTime> listOfLocalDateTimes;

        public TaskDTOModel.TaskBuilder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }
        public TaskDTOModel.TaskBuilder name(String name) {
            this.name = name;
            return this;
        }
        public TaskDTOModel.TaskBuilder description(String description) {
            this.description= description;
            return this;
        }
        public TaskDTOModel.TaskBuilder colorCategory(String colorCategory) {
            this.colorCategory = colorCategory;
            return this;
        }

        public TaskDTOModel.TaskBuilder urgency(int urgency) {
            this.urgency = urgency;
            return this;
        }
        public TaskDTOModel.TaskBuilder importance(int importance) {
            this.importance = importance;
            return this;
        }

        public TaskDTOModel.TaskBuilder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }
        public TaskDTOModel.TaskBuilder createLocalDateTime(LocalDateTime createLocalDateTime) {
            this.createLocalDateTime= createLocalDateTime;
            return this;
        }
        public TaskDTOModel.TaskBuilder listOfLocalDateTimes(List<LocalDateTime> listOfLocalDateTimes) {
            this.listOfLocalDateTimes = listOfLocalDateTimes;
            return this;
        }

        public TaskDTOModel build() {
            return new TaskDTOModel(this);
        }
    }
}


