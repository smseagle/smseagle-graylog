import React from 'react';
import cloneDeep from 'lodash/cloneDeep';
import get from 'lodash/get';

import { Input } from 'components/bootstrap';
import * as FormsUtils from 'util/FormsUtils';

const DATA_TYPES = {
  SMS: 'SMS',
  FLASHSMS: 'Flash SMS',
  MULTICHANNEL: 'Ring + SMS',
  SIGNAL: 'Signal',
  WHATSAPP: 'WhatsApp',
  RING: 'Ring call',
  TTS: 'Text-to-Speech',
  TTS_ADV: 'Text-to-Speech (Advanced)',
  ELEVENLABS: 'ElevenLabs (local)',
  ELEVENLABS_DIRECT: 'ElevenLabs (direct call)',
};

class SMSEagleNotificationForm extends React.Component {
  static defaultConfig = {
    smseagle_url: '',
    auth_token: '',
    to_number: '',
    to_contact: '',
    to_group: '',
    smseagle_type: 'SMS',
    ring_duration: 10,
    tts_model: 0,
    elevenlabs_api_key: '',
  };

  propagateChange = (key, value) => {
    const { config, onChange } = this.props;
    const nextConfig = cloneDeep(config);
    nextConfig[key] = value;
    onChange(nextConfig);
  };

  handleChange = (event) => {
    const { name } = event.target;
    const inputValue = FormsUtils.getValueFromInput(event.target);
    this.propagateChange(name, inputValue);
  };

  render() {
    const { config, validation } = this.props;
    const selectedType = config.smseagle_type || 'SMS';
    const showRingDuration = ['RING', 'MULTICHANNEL', 'TTS', 'TTS_ADV', 'ELEVENLABS', 'ELEVENLABS_DIRECT'].includes(selectedType);
    const showTtsModel = ['TTS_ADV', 'ELEVENLABS', 'ELEVENLABS_DIRECT'].includes(selectedType);
    const showElevenLabsKey = selectedType === 'ELEVENLABS_DIRECT';

    return (
      <>
        <Input
          id="smseagle_url"
          name="smseagle_url"
          label="SMSEagle URL"
          type="text"
          onChange={this.handleChange}
          bsStyle={validation.errors.smseagle_url ? 'error' : null}
          help={get(validation, 'errors.smseagle_url[0]', 'URL of your SMSEagle device (e.g. http://192.168.0.100).')}
          value={config.smseagle_url || ''}
          required
        />

        <Input
          id="auth_token"
          name="auth_token"
          label="Access Token"
          type="password"
          onChange={this.handleChange}
          bsStyle={validation.errors.auth_token ? 'error' : null}
          help={get(validation, 'errors.auth_token[0]', 'SMSEagle API access token.')}
          value={config.auth_token || ''}
          required
        />

        <Input
          id="to_number"
          name="to_number"
          label={<span>Phone number(s) <small className="text-muted">(Optional)</small></span>}
          type="text"
          onChange={this.handleChange}
          bsStyle={validation.errors.to_number ? 'error' : null}
          help={get(validation, 'errors.to_number[0]', 'Comma-separated phone numbers.')}
          value={config.to_number || ''}
        />

        <Input
          id="to_contact"
          name="to_contact"
          label={<span>Contact ID(s) <small className="text-muted">(Optional)</small></span>}
          type="text"
          onChange={this.handleChange}
          help="Comma-separated contact IDs from SMSEagle phonebook."
          value={config.to_contact || ''}
        />

        <Input
          id="to_group"
          name="to_group"
          label={<span>Group ID(s) <small className="text-muted">(Optional)</small></span>}
          type="text"
          onChange={this.handleChange}
          help="Comma-separated group IDs from SMSEagle phonebook."
          value={config.to_group || ''}
        />

        <Input
          id="smseagle_type"
          name="smseagle_type"
          label="Message type"
          type="select"
          onChange={this.handleChange}
          value={selectedType}
        >
          {Object.entries(DATA_TYPES).map(([key, label]) => (
            <option key={key} value={key}>{label}</option>
          ))}
        </Input>

        {showRingDuration && (
          <Input
            id="ring_duration"
            name="ring_duration"
            label="Ring duration (seconds)"
            type="number"
            onChange={this.handleChange}
            value={config.ring_duration || 10}
            help="Duration of the call in seconds."
          />
        )}

        {showTtsModel && (
          <Input
            id="tts_model"
            name="tts_model"
            label="TTS Model ID"
            type="number"
            onChange={this.handleChange}
            value={config.tts_model || 0}
            help="Voice model ID for TTS_ADV / ElevenLabs."
          />
        )}

        {showElevenLabsKey && (
          <Input
            id="elevenlabs_api_key"
            name="elevenlabs_api_key"
            label="ElevenLabs API Key"
            type="password"
            onChange={this.handleChange}
            value={config.elevenlabs_api_key || ''}
            help="Required for ElevenLabs (direct call)."
          />
        )}
      </>
    );
  }
}

export default SMSEagleNotificationForm;