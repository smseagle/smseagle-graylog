import React, { useState } from 'react';

import { Table, Button } from 'components/bootstrap';

const DATA_TYPE_LABELS = {
  SMS: 'SMS',
  FLASHSMS: 'Flash SMS',
  SIGNAL: 'Signal',
  WHATSAPP: 'WhatsApp',
  RING: 'Ring call',
  TTS: 'Text-to-Speech',
  TTS_ADV: 'Text-to-Speech (Advanced)',
  ELEVENLABS: 'ElevenLabs (local)',
  ELEVENLABS_DIRECT: 'ElevenLabs (direct call)',
};

const SMSEagleNotificationSummary = ({ notification, type }) => {
  const [showDetails, setShowDetails] = useState(false);
  const { config } = notification;

  return (
    <>
      <h4>{notification.title || 'SMSEagle Notification'}</h4>
      <dl>
        <dd>{type}</dd>
        <dd>
          <Button bsStyle="link" className="btn-text" bsSize="xsmall" onClick={() => setShowDetails(!showDetails)}>
            {showDetails ? 'Less details' : 'More details'}
          </Button>
          {showDetails && (
            <Table condensed hover>
              <tbody>
                <tr>
                  <td>Description</td>
                  <td>{notification.description || 'No description given'}</td>
                </tr>
                <tr>
                  <td>SMSEagle URL</td>
                  <td><code>{config.smseagle_url}</code></td>
                </tr>
                <tr>
                  <td>Type</td>
                  <td>{DATA_TYPE_LABELS[config.smseagle_type] || config.smseagle_type}</td>
                </tr>
                {config.to_number && (
                  <tr>
                    <td>Phone number(s)</td>
                    <td>{config.to_number}</td>
                  </tr>
                )}
                {config.to_contact && (
                  <tr>
                    <td>Contact ID(s)</td>
                    <td>{config.to_contact}</td>
                  </tr>
                )}
                {config.to_group && (
                  <tr>
                    <td>Group ID(s)</td>
                    <td>{config.to_group}</td>
                  </tr>
                )}
              </tbody>
            </Table>
          )}
        </dd>
      </dl>
    </>
  );
};

export default SMSEagleNotificationSummary;
