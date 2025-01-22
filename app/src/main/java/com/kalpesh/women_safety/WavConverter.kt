import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object WavConverter {
    fun pcmToWav(pcmPath: String, wavPath: String, sampleRate: Int, channels: Int, bitDepth: Int) {
        val pcmFile = File(pcmPath)
        val wavFile = File(wavPath)

        val pcmData = pcmFile.readBytes()
        val wavHeader = createWavHeader(pcmData.size, sampleRate, channels, bitDepth)

        FileOutputStream(wavFile).use { fos ->
            fos.write(wavHeader)
            fos.write(pcmData)
        }
    }

    private fun createWavHeader(
        pcmDataSize: Int,
        sampleRate: Int,
        channels: Int,
        bitDepth: Int
    ): ByteArray {
        val totalDataLen = pcmDataSize + 36
        val byteRate = sampleRate * channels * (bitDepth / 8)

        return byteArrayOf(
            // "RIFF" chunk descriptor
            'R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 'F'.code.toByte(),
            (totalDataLen and 0xff).toByte(),
            ((totalDataLen shr 8) and 0xff).toByte(),
            ((totalDataLen shr 16) and 0xff).toByte(),
            ((totalDataLen shr 24) and 0xff).toByte(),
            'W'.code.toByte(), 'A'.code.toByte(), 'V'.code.toByte(), 'E'.code.toByte(),

            // "fmt " sub-chunk
            'f'.code.toByte(), 'm'.code.toByte(), 't'.code.toByte(), ' '.code.toByte(),
            16, 0, 0, 0, // Subchunk1Size (16 for PCM)
            1, 0, // AudioFormat (1 for PCM)
            channels.toByte(), 0, // NumChannels
            (sampleRate and 0xff).toByte(),
            ((sampleRate shr 8) and 0xff).toByte(),
            ((sampleRate shr 16) and 0xff).toByte(),
            ((sampleRate shr 24) and 0xff).toByte(),
            (byteRate and 0xff).toByte(),
            ((byteRate shr 8) and 0xff).toByte(),
            ((byteRate shr 16) and 0xff).toByte(),
            ((byteRate shr 24) and 0xff).toByte(),
            (channels * (bitDepth / 8)).toByte(), 0, // BlockAlign
            bitDepth.toByte(), 0, // BitsPerSample

            // "data" sub-chunk
            'd'.code.toByte(), 'a'.code.toByte(), 't'.code.toByte(), 'a'.code.toByte(),
            (pcmDataSize and 0xff).toByte(),
            ((pcmDataSize shr 8) and 0xff).toByte(),
            ((pcmDataSize shr 16) and 0xff).toByte(),
            ((pcmDataSize shr 24) and 0xff).toByte()
        )
    }
}
