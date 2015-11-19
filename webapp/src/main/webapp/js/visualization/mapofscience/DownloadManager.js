/* $This file is distributed under the terms of the license in /doc/license.txt$ */
/**
 * Manage async download contents and make sure don't have the same 
 * download request in the same time. Provide ability to abort download.
 */

var DownloadManager = Class.extend({
	init: function() {
		this.downloadList = {};
	},
	download: function(url, success) {
		if (!this.hasKey(url)) {
			this.downloadList[url] = { success: success, // TODO Try removing this property
					jqxhr: this.startDownload(url, success, this.downloadList)};
		}
	},
	downloadAndWait: function(url, success) {
		if (!this.hasKey(url)) {
			$.ajax({
				url: url,
				async: false,
				dataType: 'json',
				success: function(countData) { success(countData); }
			});
		}
	},
	startDownload: function(url, success, downloadList) {
		 return $.getJSON(url, // TODO Not always "latest" //TODO Test on server with big file that consume 3 seconds download time. Then Keep on and off the checkbox while downloading, to verify if the duplicate happens
				function(countData) {
			        if (success) {
						success(countData);
		 			}
			        delete(downloadList[url]);
				}
			);
	},
	hasKey: function(url) {
		return (this.downloadList[url]);
	},
	abort: function(url) {
		var options = this.downloadList[url];
		if (options) {
			options.jqxhr.abort();
			delete(this.downloadList[url]);
		}
	},
	isDone: function(url) {
		return !this.hasKey(url);
	}
});