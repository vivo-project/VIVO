${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/harvester/dashboard.css" />')}

<div class="container">
    <h1 class="title-label">${scheduledTask.taskName}</h1>
    <a href="${contextPath}/etlWorkflows">
        Go back
    </a>

    <div class="module-card">
        <form method="post" action="${contextPath}/taskDetails">
            <input type="hidden" name="taskUri" value="${scheduledTask.taskUri}">
            <input type="hidden" name="moduleName" value="${module.name}">
            <input type="hidden" name="scriptPath" value="${module.path}">

            <#list module.parameters as param>
                <#if !(param.startDateAttribute?? && param.startDateAttribute)
                   && !(param.endDateAttribute?? && param.endDateAttribute)>

                    <div class="form-row">
                        <label>
                            ${param.resolvedName}
                            <#if param.required>*</#if>
                        </label>

                        <#assign inputType = "text">
                        <#if param.type == "dateTime">
                            <#assign inputType = "datetime-local">
                        </#if>

                        <#assign inputValue = scheduledTask.parameters[param.symbol]!param.defaultValue!>

                        <#if param.type == "dateTime" && inputValue?has_content>
                            <#assign inputValue = inputValue?replace("Z", "")>
                            <#if inputValue?length gt 16>
                                <#assign inputValue = inputValue?substring(0,16)>
                            </#if>
                        </#if>

                        <#if param.type == "file">
                            <input type="file"
                                   name="${param.symbol}"
                                   <#if param.required>required</#if>
                            />
                        <#elseif param.type == "select" || param.type == "graph">
                            <select name="${param.symbol}" <#if param.required>required</#if>>
                                <#list param.options as opt>
                                    <option value="${opt}"
                                        <#if opt == inputValue>selected</#if>>
                                        ${opt}
                                    </option>
                                </#list>
                            </select>
                        <#elseif param.type == "url">
                            <input type="text"
                                   name="${param.symbol}"
                                   value="${inputValue}"
                                   <#if param.required>required</#if>
                            />
                        <#else>
                            <input type="${inputType}"
                                   name="${param.symbol}"
                                   value="${inputValue}"
                                   <#if param.required>required</#if>
                            >
                        </#if>
                    </div>

                    <#if param.type == "url" && param.subfields??>
                        <#list param.subfields as sub>
                            <#if !(sub.startDateAttribute?? && sub.startDateAttribute)
                               && !(sub.endDateAttribute?? && sub.endDateAttribute)>

                                <#assign subValue = scheduledTask.parameters[sub.symbol]!sub.defaultValue!>

                                <div class="form-row" style="margin-left:20px;">
                                    <label>
                                        ${sub.resolvedName}
                                        <#if sub.required>*</#if>
                                    </label>

                                    <input type="text"
                                           name="${sub.symbol}"
                                           value="${subValue}"
                                           <#if sub.required>required</#if>
                                    >
                                </div>
                            </#if>
                        </#list>
                    </#if>
                </#if>
            </#list>

            <div class="form-row">
                <label>${i18n().recurrence_type}</label>
                <select name="recurrenceType">
                    <#list ["DAILY","WEEKLY","MONTHLY","QUARTERLY"] as rec>
                        <option value="${rec}"
                            <#if rec == scheduledTask.recurrenceType>selected</#if>>
                            ${i18n()[rec]}
                        </option>
                    </#list>
                </select>
            </div>

            <div class="form-row">
                <label>${i18n().scheduled_task_name}</label>
                <input type="text"
                       name="scheduledTaskName"
                       value="${scheduledTask.taskName}"
                       required>
            </div>

            <button type="submit" class="run-btn">
                ${i18n().save_changes}
            </button>
        </form>

        <#if module.logFiles?? && module.logFiles?size gt 0>
            <h3 class="table-label">${i18n().available_logs}</h3>

            <ul class="log-list">
                <#list module.logFiles as log>
                    <li>
                        <a href="${contextPath}/downloadWorkflowLog?module=${module.name}&file=${log}">
                            ${log}
                        </a>

                        <button
                            type="button"
                            class="delete-log-btn"
                            data-module="${module.name}"
                            data-file="${log}">
                            ${i18n().delete}
                        </button>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
</div>

<script>

document.querySelectorAll(".delete-log-btn").forEach(btn => {
    btn.addEventListener("click", () => {

        fetch(
            "${contextPath}/downloadWorkflowLog?module=" +
            btn.dataset.module +
            "&file=" +
            btn.dataset.file,
            { method:"DELETE" }
        )
        .then(() => window.location.replace(window.location.href));
    });
});

</script>
