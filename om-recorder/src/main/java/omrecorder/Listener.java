/**
 * Copyright 2017 Kailash Dabhi (Kingbull Technology)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package omrecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


final class Listener extends AbstractRecorder {
    public Listener(PullTransport pullTransport) {
        super(pullTransport);
    }

    @Override
    public void stopRecording() {
        try {
            super.stopRecording();
            writeWavHeader();
        } catch (IOException e) {
            throw new RuntimeException("Error in applying wav header", e);
        }
    }

    private void writeWavHeader() throws IOException {
        final RandomAccessFile wavFile = randomAccessFile(file);
        wavFile.seek(0); // to the beginning
        wavFile.write(new WavHeader(pullTransport.pullableSource(), file.length()).toBytes());
        wavFile.close();
    }

    private RandomAccessFile randomAccessFile(File file) {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return randomAccessFile;
    }
}