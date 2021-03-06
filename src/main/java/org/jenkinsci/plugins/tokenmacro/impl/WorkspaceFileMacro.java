/*
 * The MIT License
 *
 * Copyright 2015 acearl.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.tokenmacro.impl;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import java.io.IOException;
import org.jenkinsci.plugins.tokenmacro.DataBoundTokenMacro;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;

@Extension
public class WorkspaceFileMacro extends DataBoundTokenMacro  {
    @Parameter(required=true)
    public String path = "";
    @Parameter
    public String fileNotFoundMessage = "ERROR: File '%s' does not exist";
    

    public static final String MACRO_NAME = "FILE";

    @Override
    public boolean acceptsMacroName(String macroName) {
        return macroName.equals(MACRO_NAME);
    }

    @Override
    public String evaluate(AbstractBuild<?, ?> context, TaskListener listener, String macroName)
            throws MacroEvaluationException, IOException, InterruptedException {        
        // do some environment variable substitution
        try {
            EnvVars env = context.getEnvironment(listener);
            path = env.expand(path);
        } catch(Exception e) {
            listener.error("Error retrieving environment: %s", e.getMessage());
        }
        
        final FilePath workspace = getWorkspace(context, macroName);
        if(!workspace.child(path).exists()) {
            return String.format(fileNotFoundMessage, path);
        }

        try {
            return workspace.child(path).readToString();
        } catch (IOException e) {
            return "ERROR: File '" + path + "' could not be read";
        }
    }

    @Override
    public boolean hasNestedContent() {
        return true;
    }
}