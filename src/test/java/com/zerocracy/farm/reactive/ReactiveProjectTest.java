/**
 * Copyright (c) 2016-2017 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.farm.reactive;

import com.zerocracy.jstk.Project;
import com.zerocracy.jstk.Stakeholder;
import com.zerocracy.jstk.fake.FkProject;
import com.zerocracy.pm.ClaimOut;
import com.zerocracy.pm.Claims;
import com.zerocracy.stk.StkWipe;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link ReactiveProject}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.11
 */
public final class ReactiveProjectTest {

    /**
     * ReactiveProject can close claims.
     * @throws Exception If some problem inside
     */
    @Test
    public void closesClaims() throws Exception {
        final AtomicBoolean done = new AtomicBoolean();
        final ReactiveProject project = new ReactiveProject(
            new FkProject(),
            new StkWipe((pkt, xml) -> done.set(true))
        );
        try (final Claims claims = new Claims(project).lock()) {
            claims.add(new ClaimOut().type("hello").token("tt"));
        }
        project.close();
        try (final Claims claims = new Claims(project).lock()) {
            MatcherAssert.assertThat(
                claims.iterate(),
                Matchers.hasSize(0)
            );
        }
        MatcherAssert.assertThat(done.get(), Matchers.is(true));
    }

    /**
     * ReactiveProject can throw an exception when stakeholder fails.
     * @throws Exception If some problem inside
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwsIfStakeholderFails() throws Exception {
        final Project project = new ReactiveProject(
            new FkProject(),
            (Stakeholder) (pkt, xml) -> {
                throw new IllegalArgumentException("oops...");
            }
        );
        try (final Claims claims = new Claims(project).lock()) {
            claims.add(new ClaimOut().type("bye").token("xx"));
        }
    }

}