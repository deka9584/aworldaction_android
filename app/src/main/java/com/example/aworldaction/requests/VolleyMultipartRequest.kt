import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

open class VolleyMultipartRequest(
    url: String,
    private val listener: Response.Listener<NetworkResponse>,
    errorListener: Response.ErrorListener
) : Request<NetworkResponse>(Method.POST, url, errorListener) {

    private val twoHyphens = "--"
    private val lineEnd = "\r\n"
    private val boundary = "apiclient-" + System.currentTimeMillis()

    private val bos = ByteArrayOutputStream()
    private val dos = DataOutputStream(bos)

    private var dataPartMap: MutableMap<String, DataPart>? = null

    override fun getHeaders(): MutableMap<String, String> {
        return headers ?: super.getHeaders()
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data;boundary=$boundary"
    }

    fun setByteData(data: MutableMap<String, DataPart>) {
        dataPartMap = data
    }

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray {
        try {
            dataPartMap?.let { dataPartMap ->
                for ((key, value) in dataPartMap) {
                    buildDataPart(dos, value, key)
                }
            }

            // close multipart form data after text and file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

            return bos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return super.getBody()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return try {
            Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: NetworkResponse) {
        listener.onResponse(response)
    }

    override fun deliverError(error: VolleyError) {
        errorListener?.onErrorResponse(error)
    }

    @Throws(IOException::class)
    private fun buildDataPart(dataOutputStream: DataOutputStream, dataFile: DataPart, inputName: String) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$inputName\"; filename=\"${dataFile.fileName}\"$lineEnd")
        if (dataFile.type != null && dataFile.type!!.isNotEmpty()) {
            dataOutputStream.writeBytes("Content-Type: ${dataFile.type}$lineEnd")
        }
        dataOutputStream.writeBytes(lineEnd)

        ByteArrayInputStream(dataFile.content).use { fileInputStream ->
            var bytesRead: Int
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (fileInputStream.read(buffer).also { bytesRead = it } > 0) {
                dataOutputStream.write(buffer, 0, bytesRead)
            }
        }

        dataOutputStream.writeBytes(lineEnd)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 1024 * 1024
    }

    class DataPart(
        var fileName: String,
        var content: ByteArray,
        var type: String?
    )
}
