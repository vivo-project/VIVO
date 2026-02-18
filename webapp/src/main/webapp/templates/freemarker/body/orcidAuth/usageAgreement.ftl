    <p>You have to accept this agreement to register an account</p>
    <form action="${urls.base}/orcidAuth/callback" method="post">
        <input type="hidden" name="json_token" value='${json_token}' />
 		<input type="checkbox" name="agreements" id="agreements" style="float: left; margin-right: 10px" required>
		<#assign data_protection_url="${urls.base}/dataprotection" >
		<label for="agreements">${i18n().registration_agreement(data_protection_url)}
		</label>
        <input type="submit" class="submit" value="Register">
    </form>

