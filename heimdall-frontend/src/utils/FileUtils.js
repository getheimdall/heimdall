const APPLICATION_OCTET_STREAM = 'application/octet-stream'
const JAR_FILE = 'application/jar'

const fileDownload = (file, filename, mime) => {

    const blobFile = new Blob([file], {type: mime})
    if (typeof window.navigator.msSaveBlob !== 'undefined'){
        window.navigator.msSaveOrOpenBlob(blobFile, filename)
    } else {
        const url = window.URL.createObjectURL(blobFile);
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
}

export const FileUtils = {
    fileDownload,
    APPLICATION_OCTET_STREAM,
    JAR_FILE
}