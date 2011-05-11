/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is
 * Mozilla Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2010
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Michael Kurze (michael@thefoundation.de)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 */

package com.mozilla.bugzilla_etl.di.attachment;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.trans.steps.userdefinedjavaclass.TransformClassBase;

import com.mozilla.bugzilla_etl.di.AbstractDestination;
import com.mozilla.bugzilla_etl.model.attachment.Attachment;
import com.mozilla.bugzilla_etl.model.attachment.AttachmentVersion;
import com.mozilla.bugzilla_etl.model.attachment.AttachmentFields;

/** Send an attachment onto a PDI hop. */
public class AttachmentDestination extends AbstractDestination<Attachment> {

    public AttachmentDestination(TransformClassBase fromStep, RowMeta out) { super(fromStep, out); }

    @Override
    public void send(Attachment attachment) throws KettleException {
        for (AttachmentVersion version : attachment) {
            output.cell(AttachmentFields.Attachment.ID).set(attachment.id());
            output.cell(AttachmentFields.Attachment.BUG_ID).set(attachment.bugId());
            output.cell(AttachmentFields.Attachment.SUBMITTED_BY).set(attachment.reporter());
            output.cell(AttachmentFields.Attachment.SUBMISSION_DATE).set(attachment.creationDate());
            output.cell(AttachmentFields.Activity.MODIFICATION_DATE).set(version.from());
            output.cell(AttachmentFields.Activity.EXPIRATION_DATE).set(version.to());
            output.cell(AttachmentFields.Activity.ANNOTATION).set(version.annotation());
            output.cell(AttachmentFields.Activity.MODIFIED_BY).set(version.author());
            output.cell(AttachmentFields.Activity.PERSISTENCE_STATE).set(version.persistenceState());

            for (AttachmentFields.Facet facet : AttachmentFields.Facet.values()) {
                output.cell(facet).set(version.facets().get(facet));
            }
            for (AttachmentFields.Measurement measurement : AttachmentFields.Measurement.values()) {
                output.cell(measurement).set(version.measurements().get(measurement));
            }
            output.next();
        }
    }

}