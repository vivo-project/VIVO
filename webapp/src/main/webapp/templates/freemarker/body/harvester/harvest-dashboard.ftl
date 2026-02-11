${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/harvester/dashboard.css" />')}

<div class="container">
    <h1 class="title-label">${i18n().available_harvest_modules}</h1>

    <div class="tab-header">
        <#list modules as module>
            <button
                type="button"
                class="tab-btn"
                data-tab="tab-${module.name}">
                ${module.name}
            </button>
        </#list>
    </div>

    <#list modules as module>
        <div class="tab-panel" id="tab-${module.name}">
            <div class="module-card">
                <h2>${module.name}</h2>
                <p>${module.description}</p>

                <form method="post" action="${contextPath}/etlWorkflows">
                    <input type="hidden" name="moduleName" value="${module.name}">
                    <input type="hidden" name="scriptPath" value="${module.path}">

                    <#list module.parameters as param>
                        <div class="form-row">
                            <label>
                                ${param.name}
                                <#if param.required>*</#if>
                            </label>

                            <#assign inputType = "text">
                            <#if param.type == "dateTime">
                                <#assign inputType = "datetime-local">
                            </#if>

                            <#assign inputValue = param.defaultValue!>

                            <#if param.type == "dateTime" && inputValue?has_content>
                                <#-- remove Z -->
                                <#assign inputValue = inputValue?replace("Z", "")>

                                <#-- trim seconds -->
                                <#if inputValue?length gt 16>
                                    <#assign inputValue = inputValue?substring(0,16)>
                                </#if>
                            </#if>

                            <#if param.type == "file">
                                <input
                                    type="file"
                                    id="${param.name?html}"
                                    name="${param.symbol?html}"
                                    accept="${param.acceptType}"
                                    <#if param.required>required</#if>
                                />
                            <#elseif param.type == "select" || param.type == "graph">
                                <select
                                    name="${param.symbol}"
                                    <#if param.required>required</#if>
                                >
                                    <#list param.options as opt>
                                        <option
                                            value="${opt}"
                                            <#if opt == inputValue>selected</#if>
                                        >
                                            ${opt}
                                        </option>
                                    </#list>
                                </select>
                            <#else>
                                <input
                                    type="${inputType}"
                                    name="${param.symbol}"
                                    value="${inputValue}"
                                    <#if param.required>required</#if>
                                >
                            </#if>
                        </div>

                        <#if param.type == "url" && param.subfields??>
                            <#list param.subfields as sub>
                                <div class="form-row" style="margin-left:20px;">
                                    <label>${sub.name}</label>
                                    <input type="text" name="${sub.symbol}" value="${sub.defaultValue!}">
                                </div>
                            </#list>
                        </#if>

                    </#list>

                    <button type="submit"
                            class="run-btn"
                            data-module="${module.name}">
                        <span class="btn-text">${i18n().run_workflow}</span>
                        <span class="spinner" style="display:none;">${i18n().workflow_in_progress} ⏳</span>
                    </button>
                    <button
                        type="button"
                        class="stop-btn"
                        data-module="${module.name}"
                        style="margin-left:10px; display:none;">
                        ${i18n().stop_workflow}
                    </button>

                    <div
                        class="cli-box"
                        id="log-${module.name}"
                        style="display:none;">
                    </div>

                    <a
                        id="download-${module.name}"
                        href="${contextPath}/downloadWorkflowLog?module=${module.name}"
                        style="
                            margin-top:10px;
                            display:<#if module.tmpExists?? && module.tmpExists>inline-block<#else>none</#if>;
                        ">
                        ${i18n().download_workflow_logs}
                    </a>
                </form>
            </div>
        </div>
    </#list>
</div>

<script>

document.querySelectorAll(".run-btn").forEach(btn => {
    const module = btn.dataset.module;
    const form = btn.closest("form");
    const stopBtn = form.querySelector(".stop-btn");
    stopBtn.style.display = "none";

    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const form = e.target;
        const formData = new FormData(form);

        btn.disabled = true;
        btn.querySelector(".btn-text").style.display = "none";
        btn.querySelector(".spinner").style.display = "inline";

        fetch(form.action, {
            method: "POST",
            body: formData
        })
        .then(() => {
            stopBtn.style.display = "inline-block";
            startPolling(btn, stopBtn, module);
        })
        .catch(() => {
            btn.disabled = false;
            btn.querySelector(".btn-text").style.display = "inline";
            btn.querySelector(".spinner").style.display = "none";
        });
    });

    stopBtn.addEventListener("click", () => {
        fetch(
            "${contextPath}/stopWorkflow",
            {
                method: "POST",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },
                body: "module=" + module
            }
        );
    });
});

function startPolling(btn, stopBtn, module) {
    const logInterval = startLogPolling(module);
    const downloadLink = document.getElementById("download-" + module);

    downloadLink.style.display = "none";

    const interval = setInterval(() => {
        fetch("${contextPath}/workflowStatus?module=" + module)
            .then(r => r.json())
            .then(data => {
                if (!data.running) {
                    btn.disabled = false;
                    stopBtn.style.display = "none";
                    btn.querySelector(".btn-text").style.display = "inline";
                    btn.querySelector(".spinner").style.display = "none";

                    clearInterval(interval);
                    clearInterval(logInterval);

                    downloadLink.style.display = "inline-block";
                } else {
                    stopBtn.disabled = false;
                }
            });
    }, 2000);
}

function startLogPolling(module) {
    const logBox = document.getElementById("log-" + module);

    logBox.style.display = "block";
    logBox.textContent = "";

    return setInterval(() => {
        fetch("${contextPath}/workflowOutputLog?module=" + module)
        .then(r => r.json())
        .then(data => {
            logBox.textContent += data.log;
            logBox.scrollTop = logBox.scrollHeight;
        });

    }, 500);
}

const tabButtons = document.querySelectorAll(".tab-btn");
const tabPanels = document.querySelectorAll(".tab-panel");

function activateTab(tabId) {
    tabPanels.forEach(p => {
        p.style.display = (p.id === tabId) ? "block" : "none";
    });

    tabButtons.forEach(b => {
        b.classList.toggle(
            "active",
            b.dataset.tab === tabId
        );
    });
}

tabButtons.forEach(btn => {
    btn.addEventListener("click", () => {
        activateTab(btn.dataset.tab);
    });
});

// activate first tab by default
if (tabButtons.length > 0) {
    activateTab(tabButtons[0].dataset.tab);
}

</script>
