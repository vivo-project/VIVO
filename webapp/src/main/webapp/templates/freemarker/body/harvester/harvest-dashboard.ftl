<style>

.container {
    width: 800px;
    margin: 0 auto;
}

.module-card {
    border: 1px solid #ddd;
    padding: 20px;
    margin-bottom: 25px;
    border-radius: 8px;
    background: #fafafa;
}

.module-card h2 {
    margin-top: 0;
}

.form-row {
    margin-bottom: 12px;
}

label {
    display: block;
    font-weight: bold;
}

input {
    width: 100%;
    padding: 6px;
}

.spinner {
    margin-left: 10px;
}

</style>

<h1>Export modules</h1>

<div class="container">

<#list modules as module>

<div class="module-card">

<h2>${module.name}</h2>
<p>${module.description}</p>

<form method="post" action="${contextPath}/harvest">

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

    <#-- optionally trim seconds -->
    <#if inputValue?length gt 16>
        <#assign inputValue = inputValue?substring(0,16)>
    </#if>
</#if>

<input
    type="${inputType}"
    name="${param.symbol}"
    value="${inputValue}"
    <#if param.required>required</#if>
>

</div>

<#if param.subfields??>
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
    <span class="btn-text">Run Export</span>
    <span class="spinner" style="display:none;">⏳</span>
</button>

</form>

</div>

</#list>

</div>

<script>
document.querySelectorAll(".run-btn").forEach(btn => {

    const module = btn.dataset.module;

    btn.closest("form").addEventListener("submit", (e) => {

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
            startPolling(btn, module);
        })
        .catch(() => {
            btn.disabled = false;
            btn.querySelector(".btn-text").style.display = "inline";
            btn.querySelector(".spinner").style.display = "none";
        });
    });
});

function startPolling(btn, module) {

    const interval = setInterval(() => {

        fetch("${contextPath}/harvest-status?module=" + module)
            .then(r => r.json())
            .then(data => {
                if (!data.running) {

                    btn.disabled = false;
                    btn.querySelector(".btn-text").style.display = "inline";
                    btn.querySelector(".spinner").style.display = "none";

                    clearInterval(interval);
                }
            });

    }, 2000);
}
</script>
