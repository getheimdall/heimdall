const fileDownload = (data, filename) => {

    if (typeof window.navigator.msSaveBlob !== 'undefined') {
        window.navigator.msSaveOrOpenBlob(data, filename)
    } else {
        const url = window.URL.createObjectURL(data);
        const tempLink = document.createElement('a');
        tempLink.style.display = 'none';
        tempLink.href = url;
        tempLink.setAttribute('download', filename);

        if (typeof tempLink.download === 'undefined') {
            tempLink.setAttribute('target', '_blank');
        }

        document.body.appendChild(tempLink);
        tempLink.click();
        document.body.removeChild(tempLink);
        window.URL.revokeObjectURL(url);
    }
};

export const FileUtils = {
    fileDownload
};
